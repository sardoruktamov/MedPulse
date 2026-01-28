package api.medpulse.uz.repository;

import api.medpulse.uz.entity.DoctorDetailsEntity;
import api.medpulse.uz.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorDetailsRepository extends CrudRepository<DoctorDetailsEntity, Long> {

    // Profil ID orqali arizani topish
    Optional<DoctorDetailsEntity> findByProfileId(Integer profileId);

    // Status bo'yicha topish (Admin uchun: PENDING larni ko'rish)
    // Pageable keyinchalik qo'shiladi
}
