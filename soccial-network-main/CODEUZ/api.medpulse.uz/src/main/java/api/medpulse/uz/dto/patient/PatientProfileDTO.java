package api.medpulse.uz.dto.patient;

import api.medpulse.uz.dto.AttachDTO; // AttachDTO ni import qiling
import api.medpulse.uz.enums.BloodGroup;
import api.medpulse.uz.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class PatientProfileDTO {
    private String id;
    private String fullName;
    private LocalDate birthDate;
    private Gender gender;

    private AttachDTO photo;

    private BloodGroup bloodGroup;
    private Double weight;
    private Double height;
    private String workingBloodPressure;

    private Integer regionId;   // Frontenddan ID keladi (masalan: 12)
    private Integer districtId; // Frontenddan ID keladi (masalan: 184)
    private String address;     // "Islom Karimov ko'chasi 5-uy"

    private String allergies;            // Allergiya haqida matn
    private String emergencyContactName; // Yaqin insonining ismi/qarindoshligi
    private String emergencyContactPhone;// Yaqin insonining telefoni
}
