package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.common.MyAccountDto;
import capstone.library.dtos.common.MyBookDto;
import capstone.library.dtos.request.CreateCopiesRequestDto;
import capstone.library.dtos.request.TagCopyRequestDto;
import capstone.library.dtos.request.UpdateCopyRequest;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.CheckCopyPolicyResponseDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.*;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.BookStatus;
import capstone.library.enums.ErrorStatus;
import capstone.library.enums.RoleIdEnum;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyMapper;
import capstone.library.repositories.*;
import capstone.library.services.BookCopyService;
import capstone.library.util.tools.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static capstone.library.util.constants.ConstantUtil.UPDATE_SUCCESS;

@Service
public class BookCopyServiceImpl implements BookCopyService {
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    MyBookRepository myBookRepository;
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BorrowPolicyRepository borrowPolicyRepository;
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BookCopyMoreRepository bookCopyMoreRepository;
    @Autowired
    FeePolicyRepository feePolicyRepository;
    @Autowired
    BookCopyMapper bookCopyMapper;
    DateTimeUtils dateTimeUtils;

    private static final String PATRON_NOT_FOUND = "Cannot find this patron in database";
    private static final String ACCOUNT_NOT_FOUND = "Cannot find this account in database";
    private static final String COPY_NOT_FOUND = "Cannot find this book copy in database";
    private static final String BOOK_COPY_NOT_FOUND = "Cannot find this book copy in the database";
    private static final String BOOK_COPY_TYPE_NOT_FOUND = "Cannot find this book copy type in the database";
    private static final String BOOK_COPY = "Book copy";
    private static final String BOOK = "Book";
    private static final String POLICY_PATRON_TYPE_COPY_TYPE = "This patron cannot borrow this copy";
    private static final String POLICY_BOOK_STATUS = "This book is not in circulation";
    private static final String POLICY_COPY_STATUS = "This copy is not available";
    private static final String BOOK_DISCARD = "The book of this copy is already discarded";
    private static final String UPDATE_COPY_LOST_ERROR = "Cannot update Lost copies";
    private static final String UPDATE_COPY_DISCARD_ERROR = "Cannot update Discarded copies";
    private static final BookCopyStatus NEW_COPY_STATUS = BookCopyStatus.IN_PROCESS;

    @Override
    @Transactional
    public String createCopies(CreateCopiesRequestDto request) {
        Book book;
        BookCopyType bookCopyType;
        Account creator;

        /*Get Book from db throw exception if book is not found
        Get Copy type from db throw exception if copy type is not found
        Get Account (creator) from db throw exception if it is not found*/
        Optional<Book> bookOptional = myBookRepository.findById(request.getBookId());
        Optional<BookCopyType> bookCopyTypeOptional = bookCopyTypeRepository.findById(request.getCopyTypeId());
        Optional<Account> accountOptional = accountRepository.findById(request.getCreatorId());
        if (bookOptional.isPresent() && bookCopyTypeOptional.isPresent() && accountOptional.isPresent()) {
            book = bookOptional.get();
            bookCopyType = bookCopyTypeOptional.get();
            creator = accountOptional.get();
        } else {
            throw new ResourceNotFoundException("Book", ErrorStatus.RESOURCE_NOT_FOUND.getReason());
        }

        try {
            insertCopies(request.getBarcodes(), request.getPrice(), book, bookCopyType, creator);
            updateBookNumberOfCopy(book);
        } catch (Exception e) {
            throw new CustomException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
        }

        return "Success";
    }

    @Override
    public Page<CopyResponseDto> getCopiesList(Pageable pageable) {
        List<CopyResponseDto> response = new ArrayList<>();
        Page<BookCopy> bookCopiesPage = bookCopyRepository.findAll(pageable);
        for (BookCopy copy : bookCopiesPage.getContent()) {
            CopyResponseDto dto;
            dto = objectMapper.convertValue(copy, CopyResponseDto.class);
            dto.getBook().setAuthors(copy.getBook().getBookAuthors().
                    toString().replace("]", "").replace("[", ""));
            dto.getBook().setGenres(copy.getBook().getBookGenres().
                    toString().replace("]", "").replace("[", ""));
            dto.setCopyType(copy.getBookCopyType().getName());
            response.add(dto);
        }
//        return new PageImpl<CopyResponseDto>(response, pageable, response.size());
        return new PageImpl<CopyResponseDto>(response, pageable, bookCopiesPage.getTotalElements());
    }

    @Override
    public String tagCopy(TagCopyRequestDto request) {
        String barcode = request.getBarcode();
        String rfid = request.getRfid();

        Optional<Account> updaterOptional = accountRepository.findById(request.getUpdater());
        if (updaterOptional.isEmpty()) {
            throw new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND);
        }

        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByBarcode(barcode);
        if (bookCopyOptional.isPresent()) {
            BookCopy bookCopy = bookCopyOptional.get();
            Optional<Book> bookOptional = myBookRepository.findById(bookCopy.getBook().getId());
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                bookCopy.setRfid(rfid.toUpperCase());
                bookCopy.setUpdater(updaterOptional.get());
                if (book.getStatus().equals(BookStatus.IN_CIRCULATION)) {
                    bookCopy.setStatus(BookCopyStatus.AVAILABLE);
                } else if (book.getStatus().equals(BookStatus.OUT_OF_CIRCULATION)) {
                    bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
                } else if (book.getStatus().equals(BookStatus.LIB_USE_ONLY)) {
                    bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
                } else {
                    throw new InvalidRequestException(BOOK_DISCARD);
                }

                try {
                    bookCopyRepository.save(bookCopy);
                } catch (Exception e) {
                    throw new CustomException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
                }
            }

        } else {
            throw new ResourceNotFoundException(BOOK_COPY, COPY_NOT_FOUND + ": " + barcode);
        }
        return "Success";
    }

    @Override
    public CheckCopyPolicyResponseDto validateCopyByRFID(String rfid, int patronId) {
        boolean violatePolicy = false;
        List<String> reasons = new ArrayList<>();
        CheckCopyPolicyResponseDto response = new CheckCopyPolicyResponseDto();
        Account patron;
        BookCopy bookCopy;

        /*Get Patron and Copy*/
        Optional<Account> patronOptional = accountRepository.findById(patronId);
        if (patronOptional.isPresent()) {
            patron = patronOptional.get();
            if (patron.getRole().getId() != RoleIdEnum.ROLE_PATRON.getRoleId()) {
                throw new ResourceNotFoundException("Patron", PATRON_NOT_FOUND);
            }
        } else {
            throw new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND);
        }

        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfid);
        if (bookCopyOptional.isPresent()) {
            bookCopy = bookCopyOptional.get();
        } else {
            throw new ResourceNotFoundException("Copy", COPY_NOT_FOUND);
        }
        /*=====================*/

        /* 1. Check policy
         * 2. Check book status
         * 3. Check copy status*/
        // 1
        Optional<BorrowPolicy> borrowPolicyOptional = borrowPolicyRepository.
                findByPatronTypeIdAndBookCopyTypeId(patron.getPatronType().getId(), bookCopy.getBookCopyType().getId());
        if (borrowPolicyOptional.isEmpty()) {
            violatePolicy = true;
            reasons.add(POLICY_PATRON_TYPE_COPY_TYPE);
        }
        // 2
        if (!bookCopy.getBook().getStatus().equals(BookStatus.IN_CIRCULATION)) {
            violatePolicy = true;
            reasons.add(POLICY_BOOK_STATUS);
        }
        // 3
        if (!bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
            violatePolicy = true;
            reasons.add(POLICY_COPY_STATUS);
        }
        /*===========*/

        /*Prepare response*/
        MyBookDto myBookDto = objectMapper.convertValue(bookCopy.getBook(), MyBookDto.class);
        myBookDto.setRfid(rfid);
        response.setCopy(myBookDto);
        response.getCopy().setGenres(bookCopy.getBook().getBookGenres().toString().
                replace("]", "").replace("[", ""));
        response.getCopy().setAuthors(bookCopy.getBook().getBookAuthors().toString().
                replace("]", "").replace("[", ""));
        response.getCopy().setBarcode(bookCopy.getBarcode());
        response.setViolatePolicy(violatePolicy);
        response.setReasons(reasons);
        int borrowDuration = borrowPolicyOptional.get().getDueDuration();
        LocalDate dueAt = LocalDate.now().plusDays(borrowDuration);
        while (dueAt.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueAt.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            dueAt = dueAt.plusDays(1);
        }
        response.setDueAt(dueAt.toString());
        /*=================*/


        return response;
    }

    @Override
    public CopyResponseDto getCopyByBarcode(String barcode) {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByBarcode(barcode);
        return getCopyResponseDto(bookCopyOptional);
    }

    @Override
    public CopyResponseDto getCopyByRfid(String rfid) {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findByRfid(rfid);
        return getCopyResponseDto(bookCopyOptional);
    }

    @Override
    public String updateCopy(UpdateCopyRequest request) {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(request.getId());
        if (bookCopyOptional.isPresent()) {
            BookCopy bookCopy = bookCopyOptional.get();

            /*Cannot update DISCARD or LOSt copy*/
            if (bookCopy.getStatus().equals(BookCopyStatus.DISCARD)) {
                throw new InvalidRequestException(UPDATE_COPY_DISCARD_ERROR);
            }
            if (bookCopy.getStatus().equals(BookCopyStatus.LOST)) {
                throw new InvalidRequestException(UPDATE_COPY_LOST_ERROR);
            }

            bookCopy.setPrice(request.getPrice());

            /*If book copy is updated while in process then tag RFID instead of update RFID
             * If book is tagged in the past then proceed to update normally*/
            if (bookCopy.getStatus().equals(BookCopyStatus.IN_PROCESS)) {
                TagCopyRequestDto dto = new TagCopyRequestDto();
                dto.setRfid(request.getRfid());
                dto.setUpdater(request.getUpdater());
                dto.setBarcode(bookCopy.getBarcode());
                tagCopy(dto);
            } else {
                bookCopy.setRfid(request.getRfid());
            }

            Optional<BookCopyType> bookCopyTypeOptional = bookCopyTypeRepository.findById(request.getCopyTypeId());
            if (bookCopyTypeOptional.isPresent()) {
                bookCopy.setBookCopyType(bookCopyTypeOptional.get());
            } else {
                throw new ResourceNotFoundException("Copy type", BOOK_COPY_TYPE_NOT_FOUND);
            }
            Optional<Account> updaterOptional = accountRepository.findById(request.getUpdater());
            if (updaterOptional.isPresent()) {
                bookCopy.setUpdater(updaterOptional.get());
            } else {
                throw new ResourceNotFoundException("Account", ACCOUNT_NOT_FOUND);
            }
            try {
                bookCopyRepository.save(bookCopy);
                return "Success";
            } catch (Exception e) {
                throw new CustomException(
                        HttpStatus.INTERNAL_SERVER_ERROR, ErrorStatus.COMMON_DATABSE_ERROR.getReason(), e.getLocalizedMessage());
            }
        } else {
            throw new ResourceNotFoundException("Book Copy", BOOK_COPY_NOT_FOUND);
        }
    }

    private CopyResponseDto getCopyResponseDto(Optional<BookCopy> bookCopyOptional) {
        if (bookCopyOptional.isPresent()) {
            BookCopy copy = bookCopyOptional.get();
            CopyResponseDto dto = objectMapper.convertValue(copy, CopyResponseDto.class);
            dto.getBook().setAuthors(copy.getBook().getBookAuthors().
                    toString().replace("]", "").replace("[", ""));
            dto.getBook().setGenres(copy.getBook().getBookGenres().
                    toString().replace("]", "").replace("[", ""));
            dto.setCopyType(copy.getBookCopyType().getName());
            if (copy.getStatus().equals(BookCopyStatus.BORROWED)) {
                Optional<BookBorrowing> bookBorrowingOptional =
                        bookBorrowingRepository.findByBookCopyIdAndReturnedAtIsNullAndLostAtIsNull(copy.getId());
                if (bookBorrowingOptional.isPresent()) {
                    Account borrower = bookBorrowingOptional.get().getBorrowing().getBorrower();
                    dto.setBorrower(objectMapper.convertValue(borrower, MyAccountDto.class));
                    dto.getBorrower().setPatronTypeName(borrower.getPatronType().getName());
                    dto.getBorrower().setRoleName(borrower.getRole().getName());
                }
            }
            return dto;
        }
        throw new ResourceNotFoundException("Book Copy", BOOK_COPY_NOT_FOUND);
    }

    @Override
    public Page<BookCopyResDto> findBookCopies(String searchValue, List<String> status, Pageable pageable) {
        List<BookCopyResDto> res = new ArrayList<>();
        long totalSize = 0;
        searchValue = searchValue == null ? "" : searchValue;
        searchValue = searchValue.trim();

        List<BookCopyStatus> statusEnums = new ArrayList<>();
        if (status != null)
            status.forEach(s -> {
                if (s != null ? EnumUtils.isValidEnumIgnoreCase(BookCopyStatus.class, s.trim()) : false)
                    statusEnums.add(BookCopyStatus.valueOf(s.trim()));
                else
                    throw new InvalidRequestException(" Param [status:" + s + "] is not a valid book copy status enum.");
            });

        Page<BookCopy> books = doFindBookCopies(searchValue, statusEnums, pageable);

        totalSize = books.getTotalElements();

        for (BookCopy bookCopy : books) {
            BookCopyResDto dto = bookCopyMapper.toResDto(bookCopy);
            dto.getBook().setAuthorsString(bookCopy.getBook().getBookAuthors().toString().
                    replace("[", "").replace("]", ""));
            dto.getBook().setGenresString(bookCopy.getBook().getBookGenres().toString().
                    replace("[", "").replace("]", ""));
            dto.setBookCopyTypeDto(objectMapper.convertValue(bookCopy.getBookCopyType(), BookCopyTypeDto.class));
            res.add(dto);
        }

        for (BookCopyResDto copy : res) {
            int stockSize = bookCopyRepository.findByBookIdAndStatus(copy.getBook().getId(), BookCopyStatus.AVAILABLE).stream().map(cop -> bookCopyMapper.toResDto(cop)).collect(Collectors.toList()).size();

            if (stockSize > 0) {
                copy.getBook().setStock(stockSize);
                copy.getBook().setAvailable(true);
            } else {
                copy.getBook().setStock(0);
                copy.getBook().setAvailable(false);
            }
            if (copy.getBook().getStatus().equals(BookStatus.LIB_USE_ONLY.toString()))
                copy.getBook().setOnlyInLibrary(true);
        }

        return new PageImpl<BookCopyResDto>(res, pageable, totalSize);
    }

    private Page<BookCopy> doFindBookCopies(String searchValue, List<BookCopyStatus> statusEnums, Pageable pageable) {
        Page<BookCopy> books;
        if (searchValue.isEmpty()) {
            books = statusEnums == null || statusEnums.isEmpty() ? bookCopyRepository.findAll(pageable) : bookCopyRepository.findAllByStatusIn(statusEnums, pageable);
        } else {
            books = statusEnums == null || statusEnums.isEmpty() ? bookCopyMoreRepository.findBookCopies(searchValue, pageable) : bookCopyMoreRepository.findBookCopiesWithStatus(searchValue, statusEnums, pageable);
        }
        return books;
    }

    private void insertCopies(Set<String> barcodes, double price, Book book, BookCopyType bookCopyType, Account creator) throws Exception {
        Set<BookCopy> bookCopies = new HashSet<>();
        for (String barcode : barcodes) {
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

    private void updateBookNumberOfCopy(Book book) throws Exception {
        book.setNumberOfCopy(bookCopyRepository.findByBookId(book.getId()).size());
        myBookRepository.save(book);
    }

    @Override
    public CopyResponseDto getCopyById(Integer id) {
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(id);
        return getCopyResponseDto(bookCopyOptional);
    }

    @Override
    public String updateCopyStatusBasedOnBookStatus(BookCopy bookCopy, BookStatus bookStatus) {
        /*Update book's copies status to match new status
         * Only update status of copies inside library, borrowed copies will be updated at return.
         * Cannot update discarded or lost copies*/
        if (!bookCopy.getStatus().equals(BookCopyStatus.BORROWED) &&
                !bookCopy.getStatus().equals(BookCopyStatus.IN_PROCESS) &&
                !bookCopy.getStatus().equals(BookCopyStatus.DISCARD) &&
                !bookCopy.getStatus().equals(BookCopyStatus.LOST)) {
            if (bookStatus.equals(BookStatus.IN_CIRCULATION)) {
                bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            } else if (bookCopy.getBook().getStatus().equals(BookStatus.OUT_OF_CIRCULATION)) {
                bookCopy.setStatus(BookCopyStatus.OUT_OF_CIRCULATION);
            } else if (bookCopy.getBook().getStatus().equals(BookStatus.DISCARD)) {
                bookCopy.setStatus(BookCopyStatus.DISCARD);
            } else if (bookCopy.getBook().getStatus().equals(BookStatus.LIB_USE_ONLY)) {
                bookCopy.setStatus(BookCopyStatus.LIB_USE_ONLY);
            }
        }
        bookCopyRepository.save(bookCopy);
        return UPDATE_SUCCESS;
    }
}
