package api.medpulse.uz.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {

    @NotBlank(message = "Post ID bo'sh bo'lishi mumkin emas")
    private String postId;

    @NotBlank(message = "Izoh matni bo'sh bo'lishi mumkin emas")
    private String content;

}
