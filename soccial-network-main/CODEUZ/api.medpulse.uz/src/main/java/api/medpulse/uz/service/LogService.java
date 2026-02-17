package api.medpulse.uz.service;

import api.medpulse.uz.entity.AdminActionLogEntity;
import api.medpulse.uz.entity.BlockedIpEntity;
import api.medpulse.uz.entity.SecurityLogEntity;
import api.medpulse.uz.enums.ActionType;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.AdminActionLogRepository;
import api.medpulse.uz.repository.BlockedIpRepository;
import api.medpulse.uz.repository.SecurityLogRepository;
import api.medpulse.uz.util.SpringSecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final AdminActionLogRepository adminLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final HttpServletRequest request; // IP ni olish uchun kerak

    /**
     * 1. ADMIN ACTION LOG (Biznes logika uchun)
     * Adminlar tomonidan bajarilgan ishlar (Bloklash, Tasdiqlash, Ko'rish...)
     */
    public void createAdminLog(ActionType type, String objectId, String description) {
        // 1. Validatsiya: Description kamida 8 ta belgi bo'lishi shart!
        if (description == null || description.trim().length() < 8) {
            throw new AppBadException("Log izohi kamida 8 ta belgidan iborat bo'lishi shart!");
        }

        // 2. Kim qilayotganini aniqlash
        Integer currentAdminId = SpringSecurityUtil.getCurrentUserId();

        // 3. Entity yasash
        AdminActionLogEntity entity = new AdminActionLogEntity();
        entity.setAdminId(currentAdminId);
        entity.setActionType(type);
        entity.setObjectId(objectId); // Masalan: "User ID: 5" yoki "Patient UUID"
        entity.setDescription(description);
        entity.setIpAddress(getClientIp());
        entity.setCreatedDate(LocalDateTime.now());

        // 4. Saqlash
        adminLogRepository.save(entity);
    }

    /**
     * 2. SECURITY LOG (Hackerlar va Xavfsizlik uchun)
     * Ruxsatsiz kirish, Xato parol, Shubhali harakatlar...
     * + Avtomatik bloklash tizimini ishga tushiradi.
     */
    public void createSecurityLog(ActionType type, String emailOrUsername, String description) {
        String ip = getClientIp();
        String userAgent = request.getHeader("User-Agent"); // Qurilma turi (Browser, OS)
        String path = request.getRequestURI();              // Qaysi URL ga urildi

        // A) Security Logga yozamiz
        SecurityLogEntity entity = new SecurityLogEntity();
        entity.setActionType(type);
        entity.setIpAddress(ip);
        entity.setUserAgent(userAgent); // Hackerning qurilmasi haqida info
        entity.setRequestPath(path);
        entity.setEmailOrUsername(emailOrUsername); // Kim sifatida kirmoqchi bo'ldi?
        entity.setCreatedDate(LocalDateTime.now());

        securityLogRepository.save(entity);

        // B) TEKSHIRUV: Hujum qilyaptimi? (Avto-bloklash)
        checkAndBlockIp(ip);
    }

    /**
     * YORDAMCHI: IP ni tekshirish va bloklash (Auto-Ban System)
     */
    private void checkAndBlockIp(String ip) {
        // 1. Agar IP allaqachon qora ro'yxatda bo'lsa, qaytamiz
        if (blockedIpRepository.existsByIpAddress(ip)) {
            return;
        }

        // 2. Oxirgi 1 daqiqa ichida shu IP dan nechta xavfsizlik xatosi bo'lganini sanaymiz
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long count = securityLogRepository.countByIpAddressAndCreatedDateAfter(ip, oneMinuteAgo);

        // 3. AGAR 50 TADAN OSHSA -> BLOKLAYMIZ (Qora ro'yxatga qo'shamiz)
        if (count >= 50) {
            BlockedIpEntity blockedIp = new BlockedIpEntity();
            blockedIp.setIpAddress(ip);
            blockedIp.setReason("Avtomatik bloklash: 1 daqiqada 50+ shubhali harakat (DDoS/BruteForce)");
            blockedIp.setBlockedDate(LocalDateTime.now());
            blockedIp.setExpireDate(LocalDateTime.now().plusHours(24)); // 24 soatga bloklanadi

            blockedIpRepository.save(blockedIp);

            log.warn("DANGER! IP {} avtomatik bloklandi. Sabab: Juda ko'p shubhali so'rovlar", ip);
        }
    }


    // IP ni olish uchun yordamchi method (Proxy orqali kelsa ham to'g'ri oladi)
    private String getClientIp() {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
