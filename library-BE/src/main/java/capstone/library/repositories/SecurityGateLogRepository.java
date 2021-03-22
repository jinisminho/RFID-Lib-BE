package capstone.library.repositories;

import capstone.library.entities.SecurityGateLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SecurityGateLogRepository extends JpaRepository<SecurityGateLog, Integer> {

    @Query(value = "SELECT * " +
            "FROM security_gate_log " +
            "where logged_at between ?1 and ?2 " +
            "GROUP BY FLOOR(UNIX_TIMESTAMP(logged_at) DIV ?3), book_copy_id",
            countQuery = "SELECT count(*) from security_gate_log",
            nativeQuery = true)
    Page<SecurityGateLog> findAllByLoggedAtBetween(LocalDateTime fromDate, LocalDateTime toDate, int interval, Pageable page);

}
