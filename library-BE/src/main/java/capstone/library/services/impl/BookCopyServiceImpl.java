package capstone.library.services.impl;

import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.entities.Account;
import capstone.library.entities.Book;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyType;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.ErrorStatus;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.repositories.MyBookRepository;
import capstone.library.services.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BookCopyServiceImpl implements BookCopyService
{
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    MyBookRepository myBookRepository;
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    AccountRepository accountRepository;

    private static final BookCopyStatus NEW_COPY_STATUS = BookCopyStatus.PREPARING;

    @Override
    @Transactional
    public String createCopies(CreateCopiesRequestDto request)
    {
        Book book;
        BookCopyType bookCopyType;
        Account creator;

        /*Get Book from db throw exception if book is not found
        Get Copy type from db throw exception if copy type is not found
        Get Account (creator) from db throw exception if it is not found*/
        Optional<Book> bookOptional = myBookRepository.findById(request.getBookId());
        Optional<BookCopyType> bookCopyTypeOptional = bookCopyTypeRepository.findById(request.getCopyTypeId());
        Optional<Account> accountOptional = accountRepository.findById(request.getCreatorId());
        if (bookOptional.isPresent() && bookCopyTypeOptional.isPresent() && accountOptional.isPresent())
        {
            book = bookOptional.get();
            bookCopyType = bookCopyTypeOptional.get();
            creator = accountOptional.get();
        } else
        {
            throw new ResourceNotFoundException("Book", ErrorStatus.RESOURCE_NOT_FOUND.getReason());
        }

        try
        {
            insertCopies(request.getBarcodes(), request.getPrice(), book, bookCopyType, creator);
            updateBookNumberOfCopy(book);
        } catch (Exception e)
        {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
        }

        return "Success";
    }

    private void insertCopies(Set<String> barcodes, double price, Book book, BookCopyType bookCopyType, Account creator) throws Exception
    {
        Set<BookCopy> bookCopies = new HashSet<>();
        for (String barcode : barcodes)
        {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBook(book);
            bookCopy.setBookCopyType(bookCopyType);
            bookCopy.setPrice(price);
            bookCopy.setStatus(NEW_COPY_STATUS);
            bookCopy.setCreator(creator);
            bookCopy.setBarcode(barcode.toUpperCase().replace(" ", ""));
            bookCopies.add(bookCopy);
        }
        bookCopyRepository.saveAll(bookCopies);
    }


    private void updateBookNumberOfCopy(Book book) throws Exception
    {
        book.setNumberOfCopy(bookCopyRepository.findByBookId(book.getId()).size());
        myBookRepository.save(book);
    }
}
