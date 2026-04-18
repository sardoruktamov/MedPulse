package api.medpulse.uz.dto.comment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO {

    private Long id;
    private String postId;
    private String content;           // Frontenddagi: text
    private LocalDateTime createdDate; // Frontenddagi: date

    // --- Izoh yozgan odamning ma'lumotlari ---
    private Integer profileId;
    private String profileName;      // Frontenddagi: name
    private String profilePhotoUrl;  // Frontenddagi: avatar

}