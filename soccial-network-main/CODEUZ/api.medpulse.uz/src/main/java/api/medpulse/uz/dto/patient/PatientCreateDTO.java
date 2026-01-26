package api.medpulse.uz.dto.patient;

import api.medpulse.uz.enums.BloodGroup;
import api.medpulse.uz.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientCreateDTO {

    @NotBlank(message = "Ism bo'sh bo'lmasligi kerak")
    private String fullName;

    @NotNull(message = "Tug'ilgan sana kiritilishi shart")
    private LocalDate birthDate;

    @NotNull(message = "Jins tanlanishi shart")
    private Gender gender; // Enum: MALE, FEMALE

    // Ixtiyoriy qismlar (birdaniga qo'shib ketish uchun)
    private String photoId;
    private BloodGroup bloodGroup; // Enum
    private Double weight;
    private Double height;
    private String workingBloodPressure;
}
