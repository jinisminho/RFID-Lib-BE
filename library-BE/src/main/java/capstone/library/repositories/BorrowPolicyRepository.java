package capstone.library.repositories;

import capstone.library.entities.BorrowPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowPolicyRepository extends JpaRepository<BorrowPolicy, Integer>
{
    
}
