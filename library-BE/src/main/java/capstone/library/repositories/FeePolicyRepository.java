package capstone.library.repositories;

import capstone.library.entities.FeePolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeePolicyRepository extends JpaRepository<FeePolicy, Integer>
{
    List<FeePolicy> findAllByOrderByCreatedAtAsc();

    List<FeePolicy> findAllByOrderByCreatedAtDesc();

    Page<FeePolicy> findAllByOrderByCreatedAtDesc(Pageable pageable);


    Page<FeePolicy> findByCreatedAtBetweenOrderByCreatedAtDesc(Pageable pageable,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);

}
