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

    Optional<Account> findById(String id);

    Optional<Account> findByIdAndRoleId(Integer id, Integer roleId);

    Page<Account> findAccountsByRoleId(Integer roleId, Pageable pageable);

}
