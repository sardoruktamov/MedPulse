package api.medpulse.uz.entity;

import api.medpulse.uz.enums.ApplicationStatus;
import api.medpulse.uz.enums.DoctorDegree;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private UniversityEntity university; // M: Toshkent Tibbiyot Akademiyasi

    @Enumerated(EnumType.STRING)
    @Column(name = "degree")
    private DoctorDegree degree; // M: BACHELOR

    @Column(name = "graduated_date")
    private LocalDate graduatedDate; // Bitirgan sanasi

    @Column(name = "experience_year")
    private Integer experienceYear; // Ish tajribasi (Yil hisobida, M: 5)

    @Column(name = "current_workplace")
    private String currentWorkplace; // M: Akfa Medline

    // --- 1. DIPLOMLAR (Majburiy, Ko'p) --CascadeType.ALL- gar List ichida narsa bo‘lsa, uni ham saqlayman
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "doctor_diploms", // Alohida jadval bo'ladi
            joinColumns = @JoinColumn(name = "doctor_details_id"),
            inverseJoinColumns = @JoinColumn(name = "attach_id")
    )
    private List<AttachEntity> diplomList;

    // --- 2. SERTIFIKATLAR (Ixtiyoriy, Ko'p) ---
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "doctor_certificates", // Bu ham alohida jadval
            joinColumns = @JoinColumn(name = "doctor_details_id"),
            inverseJoinColumns = @JoinColumn(name = "attach_id")
    )
    private List<AttachEntity> certificateList;

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