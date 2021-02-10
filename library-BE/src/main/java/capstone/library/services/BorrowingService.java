package capstone.library.services;

import capstone.library.entities.Borrowing;

import java.util.Optional;

public interface BorrowingService {
    Optional<Borrowing> findById(int id);
}
