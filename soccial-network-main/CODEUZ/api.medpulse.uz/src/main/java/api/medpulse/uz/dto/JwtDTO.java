package api.medpulse.uz.dto;

import api.medpulse.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtDTO {

    private String username;
    private Integer id;
    private List<ProfileRole> roleList;
}
