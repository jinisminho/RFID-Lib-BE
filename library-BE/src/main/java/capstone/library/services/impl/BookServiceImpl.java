package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.entities.BookCopy;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.mappers.BookMapper;
import capstone.library.repositories.BookRepository;
import capstone.library.dtos.common.BookDto;
import capstone.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookCopyMapper bookCopyMapper;
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BookDto> findBooks(String searchValue) {
        return bookRepository.findBooks(searchValue).stream().map(book -> BookMapper.INSTANCE.toDto(book)).collect(Collectors.toList());
    }
    public List<BookResDto> findBooks(String searchValue) {
        List<BookResDto> res = new ArrayList<>();

    @Override
    public boolean reindexAll() {
        bookRepository.reindexAll();
        return true;
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
