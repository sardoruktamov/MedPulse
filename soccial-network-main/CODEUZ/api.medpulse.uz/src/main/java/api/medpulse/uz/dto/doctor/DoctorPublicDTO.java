package api.medpulse.uz.dto.doctor;

import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.enums.DoctorDegree;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DoctorPublicDTO {
    // oddiy foydalanuvchilar (Bemorlar) uchun Diplomsiz va Rad etish sababisiz variant

    private Long id;
    private Integer profileId;
    private String fullName;      // Profiledan olamiz
    private AttachDTO avatar;     // Profiledan olamiz

    private String speciality;
    private String universityName;
    private DoctorDegree degree;
    private String currentWorkplace;
    private Integer experienceYear;
    //  Sertifikatlar barchaga ko'rinadi
    private List<AttachDTO> certificateList;
}
