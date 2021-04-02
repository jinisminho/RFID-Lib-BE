package capstone.library.demo.repositories;

import capstone.library.demo.entities.BorrowPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowPolicyRepository extends JpaRepository<BorrowPolicy, Integer> {

    Optional<BorrowPolicy> findByPatronTypeIdAndBookCopyTypeId (Integer patronTypeId, Integer bookCopyTypeId);

    List<BorrowPolicy> findByPatronTypeId(Integer patronTypeId);

}
