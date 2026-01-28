package api.medpulse.uz.dto.doctor;

import api.medpulse.uz.enums.DoctorDegree;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class DoctorApplyDTO {
    // Ariza topshirish uchun, User to‘ldiradigan forma.

    @NotBlank(message = "Mutaxassislik bo'sh bo'lmasligi kerak")
    private String speciality;

    @NotBlank(message = "OTM nomi bo'sh bo'lmasligi kerak")
    private String universityName;

    @NotNull(message = "Daraja tanlanishi kerak")
    private DoctorDegree degree;

    private LocalDate graduatedDate;    // Bitirgan sanasi

    private Integer experienceYear;

    private String currentWorkplace;

    @NotBlank(message = "Diplom yuklanishi shart")
    private String diplomId; // Rasm ID si

    @NotNull(message = "Yuridik shartlarga rozilik kerak")
    private Boolean agreementPolicy;
}