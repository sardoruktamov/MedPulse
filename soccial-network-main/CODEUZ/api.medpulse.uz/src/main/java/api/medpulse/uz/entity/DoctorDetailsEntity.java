package api.medpulse.uz.entity;

import api.medpulse.uz.enums.ApplicationStatus;
import api.medpulse.uz.enums.DoctorDegree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_details")
@Getter
@Setter
public class DoctorDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Profile bilan bog'liqlik ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", unique = true, nullable = false)
    @JsonIgnore // JSON ichida qaytmasin (DTO ishlatamiz)
    private ProfileEntity profile;

    // --- Kasbiy Ma'lumotlar ---
    @Column(nullable = false)
    private String speciality; // M: Kardiolog

    @Column(name = "university_name")
    private String universityName; // M: Toshkent Tibbiyot Akademiyasi

    @Enumerated(EnumType.STRING)
    @Column(name = "degree")
    private DoctorDegree degree; // M: BACHELOR

    @Column(name = "graduated_date")
    private LocalDate graduatedDate; // Bitirgan sanasi

    @Column(name = "experience_year")
    private Integer experienceYear; // Ish tajribasi (Yil hisobida, M: 5)

    @Column(name = "current_workplace")
    private String currentWorkplace; // M: Akfa Medline

    // --- Diplom Rasmi (Bizning Standart) ---
    @Column(name = "diploma_id")
    @JsonIgnore
    private String diplomId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diploma_id", insertable = false, updatable = false)
    private AttachEntity diplom;

    // --- Ariza Holati ---
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;   // Ko'rib chiqilmoqda

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason;
    // Rad etilganda sabab yoziladi.
    // Qayta tahrirlaganda o'chmaydi (Admin tarixi uchun).
    // Tasdiqlanganda (Approved) null qilinadi.

    private Boolean agreementPolicy = false; // Yuridik rozilik

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate; // Qachon oxirgi marta tahrirlandi?
}