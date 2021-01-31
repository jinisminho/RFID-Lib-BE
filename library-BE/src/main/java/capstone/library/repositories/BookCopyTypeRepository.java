package capstone.library.repositories;

import capstone.library.entities.BookCopyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCopyTypeRepository extends JpaRepository<BookCopyType, Integer>
{
}
