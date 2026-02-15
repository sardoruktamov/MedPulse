package api.medpulse.uz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // Barcha fieldlar qatnashgan konstruktor yaratadi
@NoArgsConstructor  // Bo'sh konstruktor
public class AppErrorDTO {

    private String message; // "Ruxsat yo'q" yoki "Foydalanuvchi topilmadi"
    private Integer status; // 403, 404, 500 va h.k.
    private String path;    // Qaysi URL da xato bo'ldi (ixtiyoriy)
    private LocalDateTime timestamp; // Xato vaqti

    // Oddiyroq konstruktor (faqat xabar va status uchun)
    public AppErrorDTO(String message, Integer status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
