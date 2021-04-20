package capstone.library.services;

import capstone.library.dtos.response.BorrowingResDto;
import capstone.library.entities.Borrowing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BorrowingService {
    Optional<Borrowing> findById(int id);

    Page<BorrowingResDto> findAll(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
