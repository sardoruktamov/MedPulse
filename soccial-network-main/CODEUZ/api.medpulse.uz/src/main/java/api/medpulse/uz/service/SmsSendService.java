package api.medpulse.uz.service;

import api.medpulse.uz.dto.sms.SmsAuthDTO;
import api.medpulse.uz.dto.sms.SmsAuthResponseDTO;
import api.medpulse.uz.dto.sms.SmsRequestDTO;
import api.medpulse.uz.dto.sms.SmsSendResponseDTO;
import api.medpulse.uz.entity.SmsProviderTokenHolderEntity;
import api.medpulse.uz.enums.AppLanguage;
import api.medpulse.uz.enums.SmsType;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.SmsProviderTokenHolderRepository;
import api.medpulse.uz.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsSendService {
    @Value("${eskiz.url}")
    private String smsUrl;
    @Value("${eskiz.login}")
    private String accountLogin;
    @Value("${eskiz.password}")
    private String accountPassword;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsHistoryService smsHistoryService;

    @Autowired
    private ResourceBundleService bundleService;

    @Autowired
    private SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;

    Integer smsLimit = 1;



    public void sendRegistrationSms(String phoneNumber, AppLanguage lang){
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.confirmcode",lang) + ": %s";
        message = String.format(message,code);
        sendSms(phoneNumber,message,code,SmsType.REGISTRATION, lang);
    }
//    private final String messageEskiz = new SmsSendService().sendRegistrationSms();
    private SmsSendResponseDTO sendSms(String phoneNuber, String message, String code, SmsType smsType, AppLanguage lang){
        // check
        Long count = smsHistoryService.getSmsCount(phoneNuber);
        if (count >= smsLimit){
            log.warn("SMS limit reached. Phone: " + phoneNuber);
            throw new AppBadException(bundleService.getMessage("you.can.send.one.sms.code",lang));
        }
        SmsSendResponseDTO result = sendSms(phoneNuber, message, lang);
        // sms save
        smsHistoryService.created(phoneNuber,message, code, smsType);
        return result;

    }

    public void sendResetPasswordSms(String username, AppLanguage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.confirmcode",lang) + "RESET PASSWORD: %s";
        message = String.format(message,code);
        sendSms(username,message,code,SmsType.RESET_PASSWORD, lang);
    }

    public void sendUsernameChangeConfirmSms(String username, AppLanguage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.confirmcode",lang) + "RESET PASSWORD: %s";
        message = String.format(message,code);
        sendSms(username,message,code,SmsType.CHANGE_USERNAME_CONFIRM, lang);
    }
    private SmsSendResponseDTO sendSms(String phoneNumber, String message, AppLanguage lang){
        // get token
        String token = getToken();
        // header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("Authorization","Bearer " + token);
        // body
        SmsRequestDTO body = new SmsRequestDTO();
        body.setMobile_phone(phoneNumber);
        // message -> vaqtincha o'zgaruvchi TODO message SHARTNOMA TUZILGANDA O'ZGARTIRISH
//        message = bundleService.getMessage("sms.registration.confirmcode",lang);
        body.setMessage(bundleService.getMessage("sms.registration.confirmcode",lang));
        body.setFrom("4546");
        // send request
        HttpEntity<SmsRequestDTO> entity = new HttpEntity<>(body,headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(
                    smsUrl + "/message/sms/send",
                    HttpMethod.POST,
                    entity,
                    SmsSendResponseDTO.class);//smsUrlga POST request yubor ENTITYni va Stringga konvert qil

            log.error("xaaaattttttooooooo: phoneNuber: " + phoneNumber);
            log.error("xaaaattttttooooooo: message: " + message);
            log.error("xaaaattttttooooooo: tel nomer: " + body.getFrom());

            System.out.println("-------------SMS yuborildiiiii---------------");
            SmsSendResponseDTO responseDTO = new SmsSendResponseDTO();
            return response.getBody();
        }catch (HttpClientErrorException e) {
            // Eskizdan 400, 401 kabi xatolar kelsa shu yerga tushadi
            log.error("Eskiz API Error: {}", e.getResponseBodyAsString());

            // Agar balans tugagan bo'lsa, aniqroq xato tashlash yoki userga bildirish kerak
            if (e.getResponseBodyAsString().contains("fill the balance")) {
                log.error("DIQQAT! Eskiz.uz balansida pul tugadi!");
            }
            throw new AppBadException(bundleService.getMessage("error.sending.sms", lang));
        }catch (RuntimeException e){
            e.printStackTrace();
            log.error("Send SMS phone: {}, message: {}, ERROR: {}", phoneNumber,message, e.getMessage());
            throw new AppBadException(bundleService.getMessage("error.sending.sms",lang));

        }
    }

    private String getToken(){
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        if (optional.isEmpty()){
            String token = getTokenFromProvider();
            SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            System.out.println(token);
            return token;
        }
        SmsProviderTokenHolderEntity entity = optional.get();
//        LocalDateTime expDate = entity.getCreatedDate().plusMonths(1);
        if (LocalDateTime.now().isBefore(entity.getExpiredDate())){
            return entity.getToken();
        }
        // update token (agar tokenni vaqti tugagan bo`lsa yangi token oladi va update qiladi)
        String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
        return token;
    }

    private String getTokenFromProvider(){

        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);

        //1-usulda token olish
//        String response = restTemplate.postForObject(smsUrl + "/auth/login", smsAuthDTO, String.class);
//        JsonNode jsonNode = new ObjectMapper().readTree(response);
//        JsonNode data = jsonNode.get("data");
//        String token = data.get("token").asText();
//        log.info("TOKEN: " + token);
//        return token;

        try {
            // 2-usulda token olish
            SmsAuthResponseDTO response = restTemplate.postForObject(smsUrl + "/auth/login", smsAuthDTO, SmsAuthResponseDTO.class);
            log.error("G---------------/auth/login------------------nt" + response);
            return response.getData().getToken();
        } catch (RuntimeException e) {
            log.error("Get token from provider. account: {}, ERROR: {}", accountLogin, e.getMessage());
            throw new RuntimeException(e);
        }



    }


}
