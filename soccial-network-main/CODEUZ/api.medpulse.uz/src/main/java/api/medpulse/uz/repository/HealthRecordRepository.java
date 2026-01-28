package api.medpulse.uz.repository;

import api.medpulse.uz.dto.HealthRecord.HealthRecordSearchDTO;
import api.medpulse.uz.entity.HealthRecordEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthRecordRepository extends CrudRepository<HealthRecordEntity, Long> {

    // Bemor ID si bo'yicha barcha yozuvlarni topish
    // OrderByCreatedDateDesc - eng oxirgi qo'shilgan kasallik birinchi chiqadi
    List<HealthRecordEntity> findByPatientIdOrderByCreatedDateDesc(String patientId);

    // kasalliklar Ro'yxatini olish
    List<HealthRecordEntity> findByPatientIdOrderByRecordDateDesc(String patientId);

    /**
     * Jarayon bunday kechadi:1.Hibernate (ORM) bu so‘rovni o‘qiydi.
     * 2.Baza dan ma'lumotlarni oddiy "qatorlar" (array) ko‘rinishida olib keladi.
     * new so‘zini ko‘rgach, Hibernate tushunadiki: "Ha, men bu ma'lumotlarni shunchaki array qilib bermasligim kerak,
     * men Yangi Obyekt (Instance) yaratishim kerak ekan".
     * U xuddi Java kodidagi kabi new HealthRecordSearchDTO(...) konstruktorini chaqiradi va bazadan kelgan ma'lumotlarni uning ichiga joylaydi.
     */

    @Query("SELECT new api.medpulse.uz.dto.HealthRecord.HealthRecordSearchDTO(" +
            " h.id, " +
            " h.recordDate, " +
            " h.diseaseName, " +
            " h.patient.fullName, " +
            " h.patient.photo) " + // Entityni berib yuboramiz
            "FROM HealthRecordEntity h " +
            "WHERE h.patient.owner.id = :ownerId " +
            "AND (:text IS NULL OR " +
            "   (LOWER(h.diseaseName) LIKE :text OR " +
            "    LOWER(h.treatment) LIKE :text OR " +
            "    LOWER(h.doctorName) LIKE :text OR " +
            "    LOWER(h.hospitalName) LIKE :text " +
            "   )) " +
            "AND (cast(:fromDate as date) IS NULL OR h.recordDate >= :fromDate) " +
            "AND (cast(:toDate as date) IS NULL OR h.recordDate <= :toDate) " +
            "ORDER BY h.recordDate DESC")
    List<HealthRecordSearchDTO> filter(@Param("ownerId") Integer ownerId,
                                       @Param("text") String text,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate);
}
