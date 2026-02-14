package api.medpulse.uz.repository;

import api.medpulse.uz.entity.ProfileRoleEntity;
import api.medpulse.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity,Integer> {

    // Foydalanuvchining barcha rollarini o'chirish (kerak bo'lib qolishi mumkin)
    @Transactional
    @Modifying
    void deleteByProfileId(Integer integer);

    @Query("select p.roles from ProfileRoleEntity p where p.profileId = ?1")
    List<ProfileRole> getAllRolesListByProfileId(Integer profileId);

    List<ProfileRoleEntity> findByProfileId(Integer profileId);

    // Maqsad: Shu foydalanuvchida aynan shu rol bormi?
    // SQL: SELECT count(*) > 0 FROM profile_role WHERE profile_id = ? AND roles = ?
    boolean existsByProfileIdAndRoles(Integer profileId, ProfileRole role);

    // SQL: DELETE FROM profile_role WHERE profile_id = ? AND roles = ?
    @Transactional // O'chirish amali uchun shart!
    @Modifying     // Baza o'zgarayotganini bildiradi
    void deleteByProfileIdAndRoles(Integer profileId, ProfileRole roles);
}
