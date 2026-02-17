package api.medpulse.uz.repository;

import api.medpulse.uz.entity.AdminActionLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLogEntity, Long> {

    // SuperAdmin loglarni ko'rishi uchun (Sana bo'yicha kamayish tartibida)
    Page<AdminActionLogEntity> findAllByOrderByCreatedDateDesc(Pageable pageable);

    // Admin ID bo'yicha filtrlash (ixtiyoriy)
    Page<AdminActionLogEntity> findAllByAdminIdOrderByCreatedDateDesc(Integer adminId, Pageable pageable);
}
