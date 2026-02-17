package api.medpulse.uz.repository;

import api.medpulse.uz.entity.SecurityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLogEntity, Long> {
    /**
     * 1 daqiqa ichidagi xatolarni sanash uchun
     * Agar security_log da bitta IP dan 1 daqiqada 50 ta xato bo‘lsa -> blocked_ip ga tiqadi.
      */

    // SQL: SELECT count(*) FROM security_log WHERE ip_address = ? AND created_date > ?
    long countByIpAddressAndCreatedDateAfter(String ipAddress, LocalDateTime time);
}