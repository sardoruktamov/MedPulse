package api.medpulse.uz.dto.qr;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CriticalRecordDTO {
    private String diseaseName; // Entitydagi diseaseName
    private String treatment;   // Entitydagi treatment
    private String doctorName;  // Entitydagi doctorName
    private String hospitalName;// Entitydagi hospitalName
    private String date;        // Entitydagi recordDate
}
