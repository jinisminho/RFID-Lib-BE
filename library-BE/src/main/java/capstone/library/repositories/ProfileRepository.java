package capstone.library.repositories;

import capstone.library.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findById(Integer id);

    Optional<Profile> findByAccount_EmailOrAccount_Rfid(String email, String rfid);

}
