package api.medpulse.uz.dto.HealthRecord;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class HealthFilterDTO {
    private String text;        // Qidirilayotgan so'z (Dori, Kasallik, Doktor...)
    private LocalDate fromDate; // Boshlanish sanasi
    private LocalDate toDate;   // Tugash sanasi
}
