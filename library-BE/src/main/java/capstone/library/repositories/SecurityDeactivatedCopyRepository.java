package capstone.library.repositories;

import capstone.library.entities.SecurityDeactivatedCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityDeactivatedCopyRepository extends JpaRepository<SecurityDeactivatedCopy, Integer> {
    void deleteByRfid(String rfid);
}
