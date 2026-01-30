package api.medpulse.uz.entity;

import api.medpulse.uz.enums.BloodGroup;
import api.medpulse.uz.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patient_profile")
@Getter
@Setter
public class PatientProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // QR-kod uchun unikal ID

    private String fullName; // Ism-familiya
    private LocalDate birthDate; // Tug'ilgan sana

    @Enumerated(EnumType.STRING)
    private Gender gender; // Jinsi

    // Rasm uchun qism
    @Column(name = "photo_id")
    @JsonIgnore
    private String photoId; // AttachEntity ID-si

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    private AttachEntity photo; // Rasm bilan bog'liqlik

    // QR-kod ma'lumotlari
    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private Double weight;
    private Double height;
    private String workingBloodPressure;
    // TODO - QAYSI ANTIBIOTIKLARGA ALLERGIYASI BORLIGI MALUMTOINI QOSHISH KERAK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore //Bu entityni JSON ga aylantirayotganda owner maydonini tashlab ket, ichiga kirma.
    private ProfileEntity owner; // Akkaunt egasi (Ota/Ona)
}
