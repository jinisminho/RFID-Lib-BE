package capstone.library.repositories;

import capstone.library.entities.SecurityGateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SecurityGateLogRepository extends JpaRepository<SecurityGateLog, Integer> {

    Page<SecurityGateLog> findAllByLoggedAtBetween(LocalDateTime fromDate, LocalDateTime toDate, Pageable page);
    
}
