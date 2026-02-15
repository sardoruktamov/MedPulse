package api.medpulse.uz.controller;

import api.medpulse.uz.dto.AppErrorDTO;
import api.medpulse.uz.exps.AppBadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
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
    public ResponseEntity<String> handle(AppBadException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    /**
     * MAXSUS METHOD: Ruxsat etilmagan (403) xatolarni tutib olish
     * Spring Security @PreAuthorize xatolarini shu yerda ushlaymiz.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppErrorDTO> handleAccessDenied(AccessDeniedException e) {
        // Logga yozib qo'yamiz (kimdir "buzib kirishga" urindi)
        // log.warn("Ruxsatsiz urinish: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403 status
                .body(new AppErrorDTO(
                        "Sizda bu amalni bajarish uchun yetarli huquq yo'q! (Talab etiladi: SUPER_ADMIN)",
                        403
                ));
    }
}
