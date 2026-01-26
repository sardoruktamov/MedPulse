package api.medpulse.uz.dto.HealthRecord;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HealthRecordUpdateDTO {

    private String diseaseName; // Kasallik nomi
    private LocalDate recordDate; // Sana
    private String doctorName;
    private String hospitalName;
    private String treatment;
    private String note;
    private String photoId;
    private Boolean isCritical;
}
