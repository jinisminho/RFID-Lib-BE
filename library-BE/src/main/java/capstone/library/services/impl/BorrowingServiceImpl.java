package capstone.library.services.impl;

import capstone.library.entities.Borrowing;
import capstone.library.repositories.BorrowingRepository;
import capstone.library.services.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    @Autowired
    BorrowingRepository borrowingRepository;

    @Override
    public Optional<Borrowing> findById(int id) {
        return borrowingRepository.findById(id);
    }
}
