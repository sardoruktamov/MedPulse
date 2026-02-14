package api.medpulse.uz.repository;

import api.medpulse.uz.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer> {
    // Hozircha qo'shimcha metod shart emas, findAll() yetadi
    // Tartiblash bo'yicha olish:
    List<RegionEntity> findAllByOrderByNameAsc();
}
