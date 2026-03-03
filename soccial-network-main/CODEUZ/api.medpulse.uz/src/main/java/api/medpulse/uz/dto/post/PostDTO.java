package api.medpulse.uz.dto.post;

import api.medpulse.uz.dto.AttachDTO;
import api.medpulse.uz.dto.ProfileDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    private String id;

    private String title;

    private String content;

    private List<AttachDTO> mediaList;

    private LocalDateTime createdDate;

    private ProfileDTO profile;

}
