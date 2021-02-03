package capstone.library.repositories;

import capstone.library.entities.FeePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePolicyRepository extends JpaRepository<FeePolicy, Integer>
{
    List<FeePolicy> findAllByOrderByCreatedAtAsc();
}