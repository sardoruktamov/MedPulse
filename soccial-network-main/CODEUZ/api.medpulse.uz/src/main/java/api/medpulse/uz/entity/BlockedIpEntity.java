package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_ip")
@Getter
@Setter
public class BlockedIpEntity {
    // Agar security_log da bitta IP dan 1 daqiqada 50 ta xato bo‘lsa -> blocked_ip ga tiqadi.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime blockedDate = LocalDateTime.now();

    private LocalDateTime expireDate; // Qachongacha bloklangan (masalan 1 kunga)

    private String reason; // "50 marta 403 xato uchun"
}