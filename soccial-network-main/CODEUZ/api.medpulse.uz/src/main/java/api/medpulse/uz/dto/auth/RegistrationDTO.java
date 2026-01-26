package api.medpulse.uz.dto.auth;

import api.medpulse.uz.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistrationDTO {
    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "username required")
    private String username;

    @NotBlank(message = "password required")
    private String password;

    // --- YANGI QO'SHILGAN QISM ---
    @NotNull(message = "Gender required") // Enum bo'lgani uchun NotNull ishlatamiz
    private Gender gender; // Enum (MALE, FEMALE)

    @NotNull(message = "Birth date required")
    private LocalDate birthDate; // Sana (yyyy-MM-dd formatida keladi)
}
