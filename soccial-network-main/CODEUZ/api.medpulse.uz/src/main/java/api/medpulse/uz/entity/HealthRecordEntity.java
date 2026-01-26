package api.medpulse.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_record")
@Getter
@Setter
public class HealthRecordEntity {
    // (Kasallik tarixi)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diseaseName; // Kasallik nomi (Gepatit B, Qizamiq va h.k.)
    private String icdCode; // Kelajakda API uchun lug'at kodi (yashirin)

    @Column(columnDefinition = "text")
    private String note; // Bemorning shaxsiy eslatmasi yoki shifokor tavsiyasi

    private Boolean isCritical = false; // TRUE bo'lsa, QR-kod skanerlanganda birinchi bo'lib chiqadi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private PatientProfileEntity patient; // Bu yozuv qaysi oila a'zosiga tegishli ekanligi

    private LocalDateTime createdDate = LocalDateTime.now(); // Yozuv kiritilgan vaqt
}
