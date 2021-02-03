package capstone.library.repositories;

import capstone.library.entities.FeePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeePolicyRepository extends JpaRepository<FeePolicy, Integer>
{
//    List<FeePolicy> findAllByOrderByCreateAtAsc();

}
