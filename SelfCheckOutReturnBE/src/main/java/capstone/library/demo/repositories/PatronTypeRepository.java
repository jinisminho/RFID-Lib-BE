package capstone.library.demo.repositories;

import capstone.library.demo.entities.PatronType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatronTypeRepository extends JpaRepository<PatronType, Integer> {

}
