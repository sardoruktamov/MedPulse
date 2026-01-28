package api.medpulse.uz.dto.doctor;

import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.enums.ApplicationStatus;
import api.medpulse.uz.enums.DoctorDegree;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DoctorFullDTO {
    //Admin ko‘rishi uchun Bu yerda diplom va rad etish sabablari bo‘ladi.
    private Long id;
    private Integer profileId;
    private String fullName; // Profiledan olinadi

    private String speciality;
    private String universityName;
    private DoctorDegree degree;
    private String currentWorkplace;

    private AttachDTO diplom; // Diplom rasmi (URL bilan)

    private ApplicationStatus status;
    private String rejectionReason; // Admin uchun muhim

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
