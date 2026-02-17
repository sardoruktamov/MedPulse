package api.medpulse.uz.config;

import api.medpulse.uz.enums.ActionType;
import api.medpulse.uz.service.LogService;
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson kutubxonasi
import api.medpulse.uz.dto.AppErrorDTO; // Sizning DTO
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;

@Component
@RequiredArgsConstructor // Autowired o'rniga konstruktor orqali inject qilish (Best Practice)
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final LogService logService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // ------------------------------------------------------------
        // 1-QISM: USERNI ANIQLASH VA LOG YOZISH (Xavfsizlik uchun)
        // ------------------------------------------------------------

        // Kim kirayotganini aniqlaymiz (Login qilganmi yoki Anonymousmi?)
        String userId = "ANONYMOUS";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            userId = auth.getName(); // Username yoki ID
        }

        // Security Logga yozamiz (Hacker ekanligini tekshirish uchun)
        try {
            logService.createSecurityLog(
                    ActionType.UNAUTHORIZED_ACCESS,
                    userId,
                    "Ruxsatsiz kirish urinishi (403 Forbidden)"
            );
        } catch (Exception ex) {
            // Agar log yozishda xato bo'lsa, dastur to'xtab qolmasligi kerak
            log.error("Log yozishda xatolik: {}", ex.getMessage());
        }

        // ------------------------------------------------------------
        // 2-QISM: JAVOB QAYTARISH (Frontend uchun chiroyli JSON)
        // ------------------------------------------------------------

        // Status va Header
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 status
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // application/json

        // DTO yasash (Sizning kodingizdan olindi - to'liq variant)
        AppErrorDTO errorDTO = new AppErrorDTO(
                "Sizda bu amalni bajarish uchun yetarli huquq yo'q! (Access Denied)",
                403
        );

        // Qo'shimcha ma'lumotlar (Qaysi URL va Qachon?)
        errorDTO.setPath(request.getRequestURI()); // Masalan: /api/v1/admin/delete
        errorDTO.setTimestamp(LocalDateTime.now());

        // JSON ga o'girib yozish
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();

        // LocalDateTime ni JSON ga to'g'ri o'girish uchun bu modul shart!
        mapper.findAndRegisterModules();

        mapper.writeValue(out, errorDTO);
        out.flush();
    }
}