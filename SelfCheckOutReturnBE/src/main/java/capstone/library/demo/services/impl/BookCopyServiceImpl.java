package capstone.library.demo.services.impl;

import capstone.library.demo.dtos.response.ScannedBookResponse;
import capstone.library.demo.entities.BookCopy;
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
        Optional<BookCopy> copyOpt = bookCopyRepo.findByRfid(rfid);
        if(copyOpt.isPresent()){
            BookCopy copy = copyOpt.get();
            return new ScannedBookResponse(rfid, copy.getBook().getTitle());
        }else{
            return null;
        }
    }
}
