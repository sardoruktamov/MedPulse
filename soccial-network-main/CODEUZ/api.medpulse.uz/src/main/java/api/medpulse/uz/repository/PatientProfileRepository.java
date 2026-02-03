package api.medpulse.uz.repository;

import api.medpulse.uz.entity.PatientProfileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PatientProfileRepository extends CrudRepository<PatientProfileEntity, String> {

    // Ota (Owner) IDsi orqali bemor profilini o'chirish uchun metod
    // ProfileEntity ichidagi ID Integer bo'lgani uchun bu yerda Integer ishlatamiz
    void deleteByOwner_Id(Integer ownerId);

    // ID va Egasi (Owner) bo'yicha qidirish
    Optional<PatientProfileEntity> findByIdAndOwner_Id(String id, Integer ownerId);

    // Foydalanuvchining barcha profillarini olish (O'ziniki va oilasiniki)
    // Bu metod frontendga qaysi ID ni update qilish kerakligini bilish uchun kerak
    List<PatientProfileEntity> findByOwner_Id(Integer currentUserId);

    Optional<PatientProfileEntity> findByQrToken(String qrToken);
}
