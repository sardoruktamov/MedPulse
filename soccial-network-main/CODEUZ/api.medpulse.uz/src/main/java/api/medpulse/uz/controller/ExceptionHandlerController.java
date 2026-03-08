package api.medpulse.uz.controller;

import api.medpulse.uz.dto.AppErrorDTO;
import api.medpulse.uz.enums.ActionType;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Autowired
    private LogService logService;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        List<String> errors = new LinkedList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(AppBadException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    /**
     * MAXSUS METHOD: Ruxsat etilmagan (403) xatolarni tutib olish
     * Spring Security @PreAuthorize xatolarini shu yerda ushlaymiz.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorDTO> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
        // Logga yozib qo'yamiz (kimdir "buzib kirishga" urindi)
        // log.warn("Ruxsatsiz urinish: {}", e.getMessage());
        // 1. Qaysi URL ga urilganini olamiz (path: null ni to'g'irlash uchun)
        String path = request.getRequestURI();

        // 2. Kimligini aniqlaymiz (DOCTOR yoki boshqa USER tokeni bo'lgani uchun ID
        // yoki Username chiqadi)
        String userId = "ANONYMOUS";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            userId = auth.getName();
        }

        // 3. QOPQON: XAKERNi (yoki ruxsatsiz Userni) BAZAGA YOZAMIZ
        try {
            logService.createSecurityLog(
                    ActionType.UNAUTHORIZED_ACCESS,
                    userId,
                    "Ruxsatsiz kirish urinishi (403). Manba: Controller (@PreAuthorize)");
        } catch (Exception ex) {
            // Bazaga yozolmay qolsa ham dastur o'chib qolmasligi uchun try-catch qildik
            ex.printStackTrace();
        }

        // 4. FRONTEND UCHUN JAVOB TAYYORLASH
        AppErrorDTO errorDTO = new AppErrorDTO(
                "Sizda ushbu amalni bajarish uchun ruxsat yo'q!",
                403);
        errorDTO.setPath(path); // <--- Mana bu yerda URL ni berdik
        errorDTO.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }
}
