package api.medpulse.uz.dto.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequestDTO {

    @JsonProperty("mobile_phone") // JSONga "mobile_phone" bo'lib boradi
    private String mobile_phone;

    @JsonProperty("message")
    private String message;

    @JsonProperty("from")
    private String from;

}
