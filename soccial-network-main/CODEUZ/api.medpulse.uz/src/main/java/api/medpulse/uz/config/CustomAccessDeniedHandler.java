package api.medpulse.uz.config;

import com.fasterxml.jackson.databind.ObjectMapper; // Jackson kutubxonasi
import api.medpulse.uz.dto.AppErrorDTO; // Sizning DTO
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    // Bu klass Spring Security 403 xatosini tutganda nima qilish kerakligini aytadi.
    // FilterChainda sodir bo‘lgan xatolar (403) Controllerga yetib bormaydi, shuning uchun shu classni yaratdik.
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. Status va Headerlarni sozlash
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // JSON qaytaramiz

        // 2. Sizning AppErrorDTO obyektingizni yasaymiz
        AppErrorDTO errorDTO = new AppErrorDTO(
                "Sizda bu amalni bajarish uchun yetarli huquq yo'q! (Access Denied)",
                403
        );
        // timestamp va path qo'shish (agar DTO da bo'lsa)
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now());

        // 3. JSON qilib yozish (ObjectMapper orqali)
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // LocalDateTime ni to'g'ri ishlashi uchun
        mapper.writeValue(out, errorDTO);
        out.flush();
    }
}