package api.medpulse.uz.service;

import api.medpulse.uz.entity.DoctorPatientAccessEntity;
import api.medpulse.uz.entity.PatientProfileEntity;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.DoctorPatientAccessRepository;
import api.medpulse.uz.repository.PatientProfileRepository;
import api.medpulse.uz.util.RandomUtil;
import api.medpulse.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessControlService {

    private final PatientProfileRepository patientProfileRepository;
    private final DoctorPatientAccessRepository accessRepository;

    // Vaqtinchalik kodlar ombori (Kesh):  <KOD, PatientID>
    // Masalan: <"123456", "uuid-laylo-123">
    private final Map<String, AccessCodeInfo> tempCodeStorage = new ConcurrentHashMap<>();

    // Ichki class (Kod ma'lumotlari)
    private static class AccessCodeInfo {
        String patientId;
        LocalDateTime expiryTime;

        public AccessCodeInfo(String patientId, LocalDateTime expiryTime) {
            this.patientId = patientId;
            this.expiryTime = expiryTime;
        }
    }

    /**
     * 1. BEMOR UCHUN KOD YARATISH (Ota bosadi)
     */
    public String generateAccessCode(String patientId) {
        // 1. Joriy user (Ota) shu bemorga egalik qiladimi?
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();
        PatientProfileEntity patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new AppBadException("Bemor topilmadi"));

        if (!patient.getOwner().getId().equals(currentUserId)) {
            throw new AppBadException("Siz faqat o'z oila a'zolaringizga kod olib bera olasiz");
        }

        // 2. 6 xonali kod yaratamiz
        String code = RandomUtil.getRandomSmsCode(); // 6 xonali (M: 582104)

        // 3. Xotiraga saqlaymiz (2 daqiqa yashaydi)
        tempCodeStorage.put(code, new AccessCodeInfo(patientId, LocalDateTime.now().plusMinutes(2)));

        return code;
    }

    /**
     * 2. SHIFOKOR KODNI TEKSHIRISHI (Shifokor kiritadi)
     */
    public String verifyCodeAndGrantAccess(String code) {
        Integer doctorId = SpringSecurityUtil.getCurrentUserId(); // Hozirgi doktor

        // 1. Kod bormi?
        if (!tempCodeStorage.containsKey(code)) {
            throw new AppBadException("Kod noto'g'ri yoki muddati tugagan");
        }

        AccessCodeInfo info = tempCodeStorage.get(code);

        // 2. Muddatini tekshiramiz
        if (LocalDateTime.now().isAfter(info.expiryTime)) {
            tempCodeStorage.remove(code); // Tozalab tashlaymiz
            throw new AppBadException("Kod muddati tugagan. Yangi kod so'rang");
        }

        // 3. RUXSAT BERISH (Bazaga yozamiz)
        DoctorPatientAccessEntity access = new DoctorPatientAccessEntity();
        access.setDoctorId(Long.valueOf(doctorId));
        access.setPatientId(info.patientId);
        // Ruxsat 1 soat davomida amal qiladi
        access.setExpireDate(LocalDateTime.now().plusHours(1));

        accessRepository.save(access);

        // 4. Kodni ishlatib bo'ldik, o'chiramiz
        tempCodeStorage.remove(code);

        // Doktorga qaysi bemor ochilganini qaytarib beramiz (Redirect uchun)
        return info.patientId;
    }

    /**
     * 3. RUXSATNI TEKSHIRISH (Har safar tarixni ko'rayotganda ishlaydi)
     */
    public void checkDoctorAccess(String patientId) {
        Integer doctorId = SpringSecurityUtil.getCurrentUserId();

        // Bazadan qidiramiz: Shu doktor, shu bemorga, vaqti tugamagan ruxsati bormi?
        boolean hasAccess = accessRepository.existsByDoctorIdAndPatientIdAndExpireDateAfter(
                Long.valueOf(doctorId), patientId, LocalDateTime.now());

        if (!hasAccess) {
            throw new AppBadException("Sizda bu bemorni ko'rishga ruxsat yo'q yoki vaqti tugagan.");
        }
    }
}
