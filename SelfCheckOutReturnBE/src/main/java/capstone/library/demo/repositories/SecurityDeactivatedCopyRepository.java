package capstone.library.demo.repositories;

import capstone.library.demo.entities.SecurityDeactivatedCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityDeactivatedCopyRepository extends JpaRepository<SecurityDeactivatedCopy, Integer> {

    void deleteByRfid(String rfid);

    Optional<SecurityDeactivatedCopy> findByRfid (String rfid);
}
