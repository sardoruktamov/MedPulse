package api.medpulse.uz.repository;

import api.medpulse.uz.entity.UniversityEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UniversityRepository extends JpaRepository<UniversityEntity, Integer> {
    // Ismi bo'yicha tartiblab olish (Dropdown uchun)
    List<UniversityEntity> findAllByActiveTrue(Sort sort);

    boolean existsByName(String name);
}