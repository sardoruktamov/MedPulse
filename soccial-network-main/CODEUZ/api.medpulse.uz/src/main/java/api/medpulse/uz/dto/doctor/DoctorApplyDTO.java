package api.medpulse.uz.dto.doctor;

import api.medpulse.uz.enums.DoctorDegree;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

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

    @NotEmpty(message = "Kamida bitta diplom yuklanishi shart")
    private List<String> diplomPhotoIds;

    // --- SERTIFIKATLAR (Ixtiyoriy) ---
    private List<String> certificatePhotoIds; // Null yoki bo'sh bo'lishi mumkin

    @NotNull(message = "Yuridik shartlarga rozilik kerak")
    private Boolean agreementPolicy;
}