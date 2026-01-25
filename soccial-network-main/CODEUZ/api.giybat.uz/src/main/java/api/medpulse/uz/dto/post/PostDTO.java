package api.medpulse.uz.dto.post;

import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.dto.ProfileDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    private String id;

    private String title;

    private String content;

    private AttachDTO photo;

    private LocalDateTime createdDate;

    private ProfileDTO profile;

}
