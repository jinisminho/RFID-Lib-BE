package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.Book;
import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.mappers.BookMapper;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookRepository;
import capstone.library.repositories.MyBookRepository;
import capstone.library.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService
{

    @Autowired
    private BookCopyRepository bookCopyRepository;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookCopyMapper bookCopyMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MyBookRepository myBookRepository;

    @Override
    public Page<BookResDto> findBooks(String searchValue, Pageable pageable)
    {
        if (searchValue == null)
        {
            throw new MissingInputException("Missing search value for search book");
        }

        List<BookResDto> books = bookRepository.findBooks(searchValue, pageable).stream().map(book -> bookMapper.toResDto(book)).collect(Collectors.toList());

        return new PageImpl<BookResDto>(books, pageable, books.size());
    }

    @Override
    public boolean reindexAll()
    {
        bookRepository.reindexAll();
        return true;
    }

    @Override
    public List<BookResponseDto> findAllBooks(Pageable pageable)
    {
        Page<Book> books = myBookRepository.findAll(pageable);
        List<BookResponseDto> responseDtos = new ArrayList<>();

        /*Find if there's any copy of this book left
         * to check book's availability*/
        for (Book book : books.getContent())
        {
            BookResponseDto dto = objectMapper.convertValue(book, BookResponseDto.class);
            int copies = bookCopyRepository.findByBookIdAndStatus(book.getId(), BookCopyStatus.AVAILABLE).size();
            if (copies > 0)
            {
                dto.setAvailableCopies(copies);
                dto.setAvailable(true);
            } else
            {
                dto.setAvailable(false);
            }
            dto.setAuthors(book.getBookAuthors().toString().
                    replace("]", "").replace("[", ""));
            dto.setGenres(book.getBookGenres().toString().
                    replace("]", "").replace("[", ""));
            dto.setBookId(book.getId());
            responseDtos.add(dto);
        }
        return responseDtos;
    }

    @Override
    public boolean tagRfidToBookCopy(Integer bookCopyId, String rfid)
    {
        if (bookCopyId == null || rfid == null)
        {
            throw new MissingInputException("Missing input");
        }

        BookCopyDto bookCopy = bookCopyMapper.toDto(bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "BookCopy with id: " + bookCopyId + " not found")));

        if (bookCopy != null)
        {
            bookCopy.setRfid(rfid);
            BookCopy result = bookCopyMapper.toEntity(bookCopy);
            bookCopyRepository.saveAndFlush(result);
            return true;
        }

        return false;
    }

}
