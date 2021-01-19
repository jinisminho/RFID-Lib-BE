package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.entities.BookCopy;
import capstone.library.demo.exceptions.ResourceNotFoundException;
import capstone.library.demo.repositories.BookCopyRepository;
import capstone.library.demo.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookCopyServiceImpl implements BookCopyService {

    @Autowired
    BookCopyRepository bookCopyRepo;

    @Override
    public ScannedBookResponse searchBookByRfid(String rfid) {
        BookCopy copy = bookCopyRepo.findByRfid(rfid)
                .orElseThrow(() -> new ResourceNotFoundException("Book with code: " + rfid + " not found. Please contact librarian!"));
        return new ScannedBookResponse(rfid, copy.getBook().getTitle());
    }
}
