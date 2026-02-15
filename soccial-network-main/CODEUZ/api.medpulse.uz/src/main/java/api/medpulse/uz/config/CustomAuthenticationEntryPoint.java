package api.medpulse.uz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import api.medpulse.uz.dto.AppErrorDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Agar user token yubormasa yoki xato token yuborsa (401 Unauthorized),
     * u yerda ham "Empty Body" bo'lmasligi uchun buni ham qo'shib qo'ygan ma'qul.
     * FilterChainda sodir bo‘lgan xatolar (401) Controllerga yetib bormaydi, shuning uchun shu classni yaratdik.
     */

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        AppErrorDTO errorDTO = new AppErrorDTO(
                "Tizimga kirish talab etiladi! (Token xato yoki yo'q)",
                401
        );
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now());

        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.writeValue(out, errorDTO);
        out.flush();
    }
}