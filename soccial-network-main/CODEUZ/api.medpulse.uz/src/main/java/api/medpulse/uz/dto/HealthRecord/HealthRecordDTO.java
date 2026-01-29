package api.medpulse.uz.dto.HealthRecord;

import api.medpulse.uz.dto.AttachDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class HealthRecordDTO {
    private Long id;
    private String diseaseName;
    private LocalDate recordDate;
    private String doctorName;
    private String hospitalName;
    private String treatment;
    private String note;

    // Entitydagi "photo" (AttachEntity) o'rniga "AttachDTO" ishlatamiz
    private List<AttachDTO> photos; // Endi bitta obyekt emas, ro'yxat qaytadi

    private Boolean isCritical;
    private LocalDateTime createdDate;
}