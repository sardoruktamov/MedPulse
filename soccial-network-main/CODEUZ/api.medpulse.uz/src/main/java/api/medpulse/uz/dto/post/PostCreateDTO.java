package api.medpulse.uz.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
public class PostCreateDTO {

    @NotBlank(message = "Title required")
    @Length(min = 5, max = 255, message = "min-5, max-255")
    private String title;

    @NotBlank(message = "Content required")
    private String content;

    @NotNull(message = "At least one media required")
    private List<String> attachIdList; // 4 tagacha rasm yoki video IDlari

}
