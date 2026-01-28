package api.medpulse.uz.dto.HealthRecord;

import api.medpulse.uz.entity.AttachEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HealthRecordSearchDTO {

    private Long id;
    private LocalDate recordDate;
    private String diseaseName;
    private String patientFullName;

    // Bu yerda avval ID yozgan edim, keyin Service uning o'rniga URL yozib qo'yadi
    private String patientPhotoUrl;

    // Konstruktor (JPQL Query uchun kerak)
    public HealthRecordSearchDTO(Long id,
                                 LocalDate recordDate,
                                 String diseaseName,
                                 String patientFullName,
                                 AttachEntity patientPhoto) {
        this.id = id;
        this.recordDate = recordDate;
        this.diseaseName = diseaseName;
        this.patientFullName = patientFullName;

        // Bazadan faqat ID ni olib qo'yamiz (Hozircha URL emas)
        if (patientPhoto != null) {
            this.patientPhotoUrl = patientPhoto.getId();
        }
    }
}
