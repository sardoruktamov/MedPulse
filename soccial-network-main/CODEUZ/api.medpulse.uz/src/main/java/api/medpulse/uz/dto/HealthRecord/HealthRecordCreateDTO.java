package api.medpulse.uz.dto.HealthRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HealthRecordCreateDTO {

    @NotBlank(message = "Bemor (Patient) tanlanishi shart")
    private String patientId; // UUID (Otani o'zi yoki bolasi)

    @NotBlank(message = "Kasallik nomi yozilishi shart")
    private String diseaseName;

    @NotNull(message = "Sana tanlanishi shart")
    private LocalDate recordDate; // "2024-01-26" kasalga chalingan sana

    private String doctorName; // "Dr. Aliyev"
    private String hospitalName; // "Hospital City"
    private String treatment; // "Paracetamol, Ko'p suyuqlik ichish..."
    private String note; // "Isitmasi 39 ga chiqdi" yoki bemorning holati, kasallik asoratlari

    private String photoId;
    private Boolean isCritical = false; // QR kode uchun
}
