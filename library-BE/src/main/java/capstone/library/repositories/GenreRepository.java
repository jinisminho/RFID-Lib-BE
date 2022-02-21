package capstone.library.repositories;

import capstone.library.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    List<Genre> findByOrderByDdcAsc();

    Optional<Genre> findByDdc(Double ddc);
}
