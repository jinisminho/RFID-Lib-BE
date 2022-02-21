package capstone.library.repositories;

import capstone.library.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Page<Author> findByNameLikeAndCountryAndBirthYear(String name, String country, int birthYear, Pageable pageable);

    Page<Author> findByNameLikeAndCountry(String name, String country, Pageable pageable);

    Page<Author> findByNameLikeAndBirthYear(String name, int bỉthYear, Pageable pageable);

    Page<Author> findByNameLike(String name, Pageable pageable);

    Page<Author> findByCountryAndBirthYear(String country, int bỉthYear, Pageable pageable);

    Page<Author> findByCountry(String country, Pageable pageable);

    Page<Author> findByBirthYear(int bỉthYear, Pageable pageable);

}
