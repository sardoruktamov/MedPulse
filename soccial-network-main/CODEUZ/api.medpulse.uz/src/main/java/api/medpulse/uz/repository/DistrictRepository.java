package api.medpulse.uz.repository;

import api.medpulse.uz.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, Integer> {
    // Shu viloyatga tegishli tumanlarni olib berish
    List<DistrictEntity> findByRegionIdOrderByNameAsc(Integer regionId);
}
