package api.medpulse.uz.repository;

import api.medpulse.uz.entity.PatientProfileEntity;
import org.springframework.data.repository.CrudRepository;

public interface PatientProfileRepository extends CrudRepository<PatientProfileEntity, String> {

    // Ota (Owner) IDsi orqali bemor profilini o'chirish uchun metod
    // ProfileEntity ichidagi ID Integer bo'lgani uchun bu yerda Integer ishlatamiz
    void deleteByOwner_Id(Integer ownerId);
}
