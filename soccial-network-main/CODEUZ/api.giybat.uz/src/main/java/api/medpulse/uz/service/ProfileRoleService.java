package api.medpulse.uz.service;

import api.medpulse.uz.entity.ProfileRoleEntity;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileRoleService {

    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void create(Integer profileId, ProfileRole role){
        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setProfileId(profileId);
        entity.setRoles(role);
        entity.setCreatedDate(LocalDateTime.now());
        profileRoleRepository.save(entity);
    }

    public void deleteRoles(Integer profileRoleId) {
        profileRoleRepository.deleteByProfileId(profileRoleId);
    }
}
