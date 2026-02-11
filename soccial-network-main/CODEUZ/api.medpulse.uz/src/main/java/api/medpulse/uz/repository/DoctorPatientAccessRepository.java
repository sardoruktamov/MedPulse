package api.medpulse.uz.repository;

import api.medpulse.uz.entity.DoctorPatientAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DoctorPatientAccessRepository extends JpaRepository<DoctorPatientAccessEntity, Long> {

    /**
     * Maqsadi: Shu doktorning (doctorId), shu bemorga (patientId)
     * hozirgi vaqtdan (now) keyin tugaydigan (hali kuchi bor) ruxsati bormi?
     * * SQL tarjimasi:
     * SELECT COUNT(*) > 0
     * FROM doctor_patient_access
     * WHERE doctor_id = ? AND patient_id = ? AND expire_date > ?
     */
    boolean existsByDoctorIdAndPatientIdAndExpireDateAfter(Long doctorId, String patientId, LocalDateTime now);
}
