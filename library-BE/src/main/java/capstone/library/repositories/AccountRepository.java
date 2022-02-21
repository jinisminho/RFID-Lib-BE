package capstone.library.repositories;

import capstone.library.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByRfid(String rfid);

    Optional<Account> findByRfidOrEmail(String rfid, String Email);

    Optional<Account> findById(String id);

    Optional<Account> findByIdAndRoleId(Integer id, Integer roleId);

    Page<Account> findByEmailContainsOrProfileFullNameContains(String email, String fullName, Pageable pageable);

    Page<Account> findAccountsByRoleId(Integer roleId, Pageable pageable);

    Optional<Account> findByEmail(String email);

    Page<Account> findByEmailContainsAndRoleId(String email, int roleId, Pageable pageable);

    Optional<Account> getTopByPatronType_Id(int id);

}
