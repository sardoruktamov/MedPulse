package api.medpulse.uz.entity;

import api.medpulse.uz.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "doctor_details")
@Getter
@Setter
public class DoctorDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String speciality; // Mutaxassisligi
    private String experienceYear; // Ish tajribasi
    private String universityName; // OTM nomi
    private String currentWorkplace; // Ish joyi
    private String diplomaAttachId; // Diplom rasmi ID-si
    private Boolean agreementPolicy = true; // Yuridik tasdiq

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // PENDING, APPROVED, REJECTED

    @Column(name = "rejection_reason", columnDefinition = "text")
    private String rejectionReason; // Admin tomonidan rad etish sababi (faqat REJECTED bo'lganda to'ldiriladi)

    @OneToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile; // Akkaunt bilan bog'liqlik
}
