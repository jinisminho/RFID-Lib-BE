package capstone.library.repositories;

import capstone.library.entities.BorrowPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowPolicyRepository extends JpaRepository<BorrowPolicy, Integer>
{
    Optional<BorrowPolicy> findByPatronTypeIdAndBookCopyTypeId(Integer patronTypeId, Integer bookCopyTypeId);

    Page<BorrowPolicy> findByPatronTypeId (Pageable pageable,Integer patronTypeId);

    Page<BorrowPolicy> findByBookCopyTypeId (Pageable pageable,Integer bookCopyTypeId);

    Page<BorrowPolicy> findByPatronTypeIdAndBookCopyTypeId(Pageable pageable, Integer patronTypeId, Integer bookCopyTypeId);
}
