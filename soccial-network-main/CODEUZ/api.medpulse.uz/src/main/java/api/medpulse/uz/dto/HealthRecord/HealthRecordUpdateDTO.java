package api.medpulse.uz.dto.HealthRecord;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class HealthRecordUpdateDTO {

    private String diseaseName; // Kasallik nomi
    private LocalDate recordDate; // Sana
    private String doctorName;
    private String hospitalName;
    private String treatment;
    private String note;
    private List<String> photoIds; // Yangi ro'yxat (Eskisini to'liq almashtiradi)
    private Boolean isCritical;
}
