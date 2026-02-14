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
    // adress
    private Integer regionId;   // Frontenddan ID keladi (masalan: 12)
    private Integer districtId; // Frontenddan ID keladi (masalan: 184)
    private String address;     // "Islom Karimov ko'chasi 5-uy"

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
