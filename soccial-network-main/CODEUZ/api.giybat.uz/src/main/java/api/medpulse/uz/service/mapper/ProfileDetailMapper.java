package api.medpulse.uz.service.mapper;

import api.medpulse.uz.enums.GeneralStatus;

import java.time.LocalDateTime;

public interface ProfileDetailMapper {

    Integer getId();
    String getName();
    String getUsername();
    String getPhotoId();
    GeneralStatus getStatus();
    LocalDateTime getCreatedDate();
    Long getPostCount();
    String getRoles();


}
