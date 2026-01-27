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
}
