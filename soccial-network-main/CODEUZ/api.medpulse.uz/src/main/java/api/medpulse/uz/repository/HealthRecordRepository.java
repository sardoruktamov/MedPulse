package api.medpulse.uz.repository;

import api.medpulse.uz.entity.HealthRecordEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthRecordRepository extends CrudRepository<HealthRecordEntity, Long> {

    // Bemor ID si bo'yicha barcha yozuvlarni topish
    // OrderByCreatedDateDesc - eng oxirgi qo'shilgan kasallik birinchi chiqadi
    List<HealthRecordEntity> findByPatientIdOrderByCreatedDateDesc(String patientId);

    // kasalliklar Ro'yxatini olish
    List<HealthRecordEntity> findByPatientIdOrderByRecordDateDesc(String patientId);
}
