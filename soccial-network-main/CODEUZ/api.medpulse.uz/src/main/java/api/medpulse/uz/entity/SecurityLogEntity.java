package api.medpulse.uz.entity;

import api.medpulse.uz.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_log")
@Getter
@Setter
public class SecurityLogEntity {
    // Hacker harakatlari, Agar Xavfsizlik bo‘lsa -> security_log ga yozadi.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent; // Qurilma haqida ma'lumot (Browser, OS)

    private String requestPath; // Qaysi URL ga urindi?

    private String emailOrUsername; // Agar login qilishgauringan bo'lsa

    private LocalDateTime createdDate = LocalDateTime.now();
}