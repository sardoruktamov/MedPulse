package api.medpulse.uz.dto.patient;

import api.medpulse.uz.enums.BloodGroup;
import api.medpulse.uz.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientUpdateDTO {
    // Qaysi profilni o'zgartirmoqchi ekanligi (UUID)
    // Buni URL path variable orqali olganimiz ma'qul, shuning uchun bu yerda shart emas, lekin xavfsizlik uchun tursa ham bo'ladi.

    private String fullName; // Ismini o'zgartirishi mumkin (xatolik bo'lsa)
    private LocalDate birthDate;
    private Gender gender;

    private String allergies;            // Allergiya haqida matn
    private String emergencyContactName; // Yaqin insonining ismi/qarindoshligi
    private String emergencyContactPhone;// Yaqin insonining telefoni

    // Tibbiy ma'lumotlar
    private String photoId; // Rasm yuklagandan keyin keladigan ID
    private BloodGroup bloodGroup; // qon guruxi
    private Double weight; // kg
    private Double height; // sm
    private String workingBloodPressure; // "120/80"
}
