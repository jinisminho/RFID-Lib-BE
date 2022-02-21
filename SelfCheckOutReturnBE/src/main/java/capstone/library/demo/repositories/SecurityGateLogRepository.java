package capstone.library.demo.repositories;

import capstone.library.demo.entities.SecurityGateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityGateLogRepository extends JpaRepository<SecurityGateLog, Integer> {
}
