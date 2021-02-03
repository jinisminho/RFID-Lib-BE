package capstone.library.demo.repositories;

import capstone.library.demo.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByRfidAndIsActive (String rfid, boolean isActive);

    Optional<Account> findByEmail (String email);
}
