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

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies; // Masalan: "Penitsillin, Chang, Tutun"

    // 1. VILOYAT (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    // 2. TUMAN (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private DistrictEntity district;

    // 3. KO'CHA VA UY (Oddiy matn)
    // Masalan: "A.Temur ko'chasi, 15-uy, 4-xonadon"
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName; // Kimligi (Masalan: Otasi - Eshmatov Toshmat)

    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone; // Bog'lanish uchun raqam: +998901234567

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore //Bu entityni JSON ga aylantirayotganda owner maydonini tashlab ket, ichiga kirma.
    private ProfileEntity owner; // Akkaunt egasi (Ota/Ona)

    // QR kod uchun qisqa token
    @Column(name = "qr_token", length = 10, unique = true)
    private String qrToken;
}
