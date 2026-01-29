package api.medpulse.uz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "health_record")
@Getter
@Setter
public class HealthRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Asosiy ma'lumotlar
    @Column(nullable = false)
    private String diseaseName; // Kasallik nomi (M: Gripp)

    @Column(name = "record_date")
    private LocalDate recordDate; // Qachon kasal bo'ldi? (Foydalanuvchi tanlaydi)

    // 2. Doktor va Klinika (Qo'shimcha takliflar bilan)
    private String doctorName; // Doktor Ismi (Qo'lda yoziladi)
    private String hospitalName; // Klinika nomi (Qo'lda yoziladi)

    @Column(columnDefinition = "text")
    private String treatment; // Davolash (Dori-darmonlar ro'yxati)

    @Column(columnDefinition = "text")
    private String note; // Qo'shimcha ixtiyoriy izoh

    // --- 3. RASMLAR (MULTIPLE UPLOAD) ---
    // Oldingi "photoId" va "photo" o'rniga:
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "health_record_photos", // Oraliq jadval nomi
            joinColumns = @JoinColumn(name = "health_record_id"), // Record ID
            inverseJoinColumns = @JoinColumn(name = "photo_id")   // Rasm ID
    )
    private List<AttachEntity> photos;
    // ------------------------------------


    private Boolean isCritical = false; // QR-kod uchun
    private LocalDateTime createdDate = LocalDateTime.now(); // Tizimga yozilgan vaqt

    // 4. Bog'liqlik
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    private PatientProfileEntity patient;
}


//    // 3. Tizim ma'lumotlari
//    // 1. Bazaga yozish uchun (JSON da ko'rinmasin)
//    @Column(name = "photo_id")
//    @JsonIgnore
//    private String photoId;
//
//    // 2. O'qish uchun (Frontendga to'liq obyekt boradi)
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
//    private AttachEntity photo;
