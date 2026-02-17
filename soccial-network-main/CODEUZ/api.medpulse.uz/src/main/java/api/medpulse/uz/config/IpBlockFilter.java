package api.medpulse.uz.config;

import api.medpulse.uz.repository.BlockedIpRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IpBlockFilter extends OncePerRequestFilter {

    private final BlockedIpRepository blockedIpRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ip = request.getRemoteAddr(); // IP ni olamiz

        // Agar IP qora ro'yxatda bo'lsa -> Darhol to'xtatamiz
        if (blockedIpRepository.existsByIpAddress(ip)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("YOUR IP IS BLOCKED due to suspicious activity.");
            return; // Controllerga o'tkazmaymiz!
        }

        filterChain.doFilter(request, response); // O'taversin
    }
}