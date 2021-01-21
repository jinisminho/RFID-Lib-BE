package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.entities.BookCopy;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Override
    public List<BookResDto> findBooks(String searchValue) {
        List<BookResDto> res = new ArrayList<>();


        return res;
    }

    @Override
    public boolean tagRfidToBookCopy(Integer bookCopyId, String rfid) {
        if (bookCopyId == null || rfid == null) {
            throw new MissingInputException("Missing input");
        }

        BookCopyDto bookCopy = bookCopyMapper.toDto(bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "BookCopy with id: " + bookCopyId + " not found")));

        if (bookCopy != null) {
            bookCopy.setRfid(rfid);
            BookCopy result = bookCopyMapper.toEntity(bookCopy);
            bookCopyRepository.saveAndFlush(result);
            return true;
        }

        return false;
    }

}
