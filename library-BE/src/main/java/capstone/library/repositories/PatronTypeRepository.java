package capstone.library.repositories;

import capstone.library.entities.PatronType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatronTypeRepository extends JpaRepository<PatronType, Integer>
{

    Page<PatronType> findByNameContains(Pageable pageable, String name);
}
