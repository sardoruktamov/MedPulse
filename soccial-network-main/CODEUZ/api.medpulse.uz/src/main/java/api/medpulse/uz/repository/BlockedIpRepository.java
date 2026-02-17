package api.medpulse.uz.repository;

import api.medpulse.uz.entity.BlockedIpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIpEntity, Integer> {
    boolean existsByIpAddress(String ipAddress);
}
