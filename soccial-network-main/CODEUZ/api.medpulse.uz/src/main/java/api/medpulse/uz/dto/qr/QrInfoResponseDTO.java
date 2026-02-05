package api.medpulse.uz.dto.qr;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class QrInfoResponseDTO {
    // Shaxsiy
    private String fullName;
    private String photoUrl; // Rasm URLi
    private String birthDate;

    // Tibbiy
    private String bloodGroup;
    private Double weight;
    private Double height;
    private String workingBloodPressure;
    private String allergies;

    // SOS
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Kritik kasalliklar
    private List<CriticalRecordDTO> criticalRecords;
}
