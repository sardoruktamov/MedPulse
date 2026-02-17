package api.medpulse.uz.entity;

import api.medpulse.uz.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_action_log")
@Getter
@Setter
public class AdminActionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Integer adminId; // Kim qildi?

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType; // Nima qildi?

    @Column(name = "object_id")
    private String objectId; // Kimni ustida? (User ID yoki Patient ID)

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Sababi (Min 8 belgi)

    @Column(name = "ip_address")
    private String ipAddress; // Qayerdan?

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
