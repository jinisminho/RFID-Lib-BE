package capstone.library.services.impl;

import capstone.library.dtos.common.BookBorrowingDto;
import capstone.library.dtos.request.ConfirmLostBookRequest;
import capstone.library.dtos.response.BookLostResponse;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookLostReport;
import capstone.library.enums.BookCopyStatus;
import capstone.library.enums.LostBookStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.*;
import capstone.library.services.BookLostReportService;
import capstone.library.services.MailService;
import capstone.library.util.tools.CommonUtil;
import capstone.library.util.tools.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;
import static capstone.library.util.constants.ConstantUtil.UPDATE_SUCCESS;


@Service
public class BookLostReportServiceImpl implements BookLostReportService {
    @Autowired
    private BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    private FeePolicyRepository feePolicyRepository;
    @Autowired
    private BookLostReportRepository bookLostReportRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BOOK_BORROWING_NOT_FOUND_ERROR = "Cannot find this borrowing section";

    //Tram added overdueDays and overdueFee
    @Override
    public LostBookFineResponseDto getLostBookFine(int bookLostReportId) {
        BookLostReport bookLostReport = bookLostReportRepository
                .findById(bookLostReportId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Lost Report",
                        "Cannot find book lost report with id: " + bookLostReportId));

        BookBorrowing bookBorrowing = bookLostReport.getBookBorrowing();

        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        int tmpFine = (int) dateTimeUtils.getOverdueDays(LocalDate.now(), bookBorrowing.getDueAt());
        int overdueDays = 0;
        double overdueFee = 0.0;
        if (tmpFine > 0) {
            overdueDays = tmpFine;
            overdueFee = CommonUtil.calculateOverdueFine(bookBorrowing.getFeePolicy(), bookBorrowing.getBookCopy().getPrice(), overdueDays);
        }

        String authors = bookBorrowing.getBookCopy().getBook().getBookAuthors()
                .stream()
                .map(a -> a.getAuthor().getName())
                .collect(Collectors.joining(", "));

        LostBookFineResponseDto response = new LostBookFineResponseDto();
        response.setBookBorrowingInfo(objectMapper.convertValue(bookBorrowing, BookBorrowingDto.class));
        response.getBookBorrowingInfo().getBookCopy().getBook().setAuthors(authors);
        response.setLostBookFineInMarket(calculateLostFineInMarket(bookBorrowing));
        response.setLostBookFineNotInMarket(calculateLostFineNotInMarket(bookBorrowing));
        response.setOverdueDays(overdueDays);
        response.setOverdueFee(overdueFee);
        response.setId(bookLostReport.getId());
        return response;
    }

    @Override
    @Transactional
    public String confirmBookLost(ConfirmLostBookRequest lostBook) {
        if (lostBook == null) {
            throw new MissingInputException("missing lost book");
        }
        Account auditor = accountRepository
                .findById(lostBook.getAuditorId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot found account with id: " + lostBook.getAuditorId()));

        BookLostReport bookLost = bookLostReportRepository
                .findById(lostBook.getBookLostReportId())
                .orElseThrow(() -> new ResourceNotFoundException("Book Lost Report",
                        "Cannot find book lost report with id: " + lostBook.getBookLostReportId()));

        bookLost.setFine(lostBook.getFine());
        bookLost.setLibrarian(auditor);
        bookLost.setStatus(LostBookStatus.CONFIRMED);
        bookLost.setReason(lostBook.getReason());
        bookLostReportRepository.save(bookLost);
        //email to patron
        mailService.sendLostBookFine(bookLost);
        return UPDATE_SUCCESS;
    }

    //when patron request lost book
    @Override
    @Transactional
    public String reportLostByPatron(int bookBorrowingId) {
        BookBorrowing borrowing = bookBorrowingRepository
                .findById(bookBorrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Borrowing",
                        "Cannot find book borrowing with id: " + bookBorrowingId));

        LocalDateTime lostAt = LocalDateTime.now();
        BookLostReport bookLostReport = new BookLostReport();
        bookLostReport.setLostAt(lostAt);
        bookLostReport.setFine(0.0);
        bookLostReport.setBookBorrowing(borrowing);
        bookLostReport.setStatus(LostBookStatus.PENDING);
        bookLostReportRepository.save(bookLostReport);
        borrowing.setLostAt(lostAt);
        bookBorrowingRepository.save(borrowing);
        BookCopy copy = borrowing.getBookCopy();
        copy.setStatus(BookCopyStatus.LOST);
        bookCopyRepository.save(copy);
        return CREATE_SUCCESS;
    }

    @Override
    public Page<BookLostResponse> findBookLostByStatus(LostBookStatus status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new MissingInputException("missing startDate or endDate");
        }
        if (startDate.isAfter(endDate)) {
            LocalDateTime tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }
        return bookLostReportRepository
                .findByStatusAndLostAtBetweenOrderByLostAtDesc(status, startDate, endDate, pageable)
                .map(this::mapBookLostEntityToBookLostDto);
    }

    @Override
    public Page<BookLostResponse> findBookLostInPeriod(LocalDateTime startDate,
                                                       LocalDateTime endDate,
                                                       Pageable pageable) {
        if (startDate == null || endDate == null) {
            throw new MissingInputException("missing startDate or endDate");
        }
        if (startDate.isAfter(endDate)) {
            LocalDateTime tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }
        return bookLostReportRepository
                .findByLostAtBetweenOrderByLostAtDesc(startDate, endDate, pageable)
                .map(this::mapBookLostEntityToBookLostDto);
    }

    private BookLostResponse mapBookLostEntityToBookLostDto(BookLostReport entity) {
        BookLostResponse dto = new BookLostResponse();
        dto.setId(entity.getId());
        dto.setLostAt(entity.getLostAt());
        dto.setBorrowedAt(entity.getBookBorrowing().getBorrowing().getBorrowedAt());
        dto.setFine(entity.getFine());
        dto.setReason(entity.getReason());
        dto.setBorrowerEmail(entity.getBookBorrowing().getBorrowing().getBorrower().getEmail());
        dto.setBarcode(entity.getBookBorrowing().getBookCopy().getBarcode());
        dto.setTitle(entity.getBookBorrowing().getBookCopy().getBook().getTitle());
        dto.setSubtitle(entity.getBookBorrowing().getBookCopy().getBook().getSubtitle());
        dto.setEdition(entity.getBookBorrowing().getBookCopy().getBook().getEdition());
        dto.setISBN(entity.getBookBorrowing().getBookCopy().getBook().getIsbn());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    private double calculateLostFineInMarket(BookBorrowing bookBorrowing) {
        return bookBorrowing.getBookCopy().getPrice() + bookBorrowing.getFeePolicy().getDocumentProcessing_Fee();
    }

    private double calculateLostFineNotInMarket(BookBorrowing bookBorrowing) {
        return bookBorrowing.getBookCopy().getPrice() * bookBorrowing.getFeePolicy().getMissingDocMultiplier();
    }

    //
    @Override
    public Page<BookLostResponse> findBookLostOfPatron(LocalDateTime startDate,
                                                       LocalDateTime endDate,
                                                       Pageable pageable,
                                                       int patronId) {
        if (startDate != null || endDate != null) {
            if (startDate.isAfter(endDate)) {
                LocalDateTime tmp = startDate;
                startDate = endDate;
                endDate = tmp;
            }
            return bookLostReportRepository
                    .findByBookBorrowing_Borrowing_Borrower_IdAndLostAtBetweenOrderByLostAtDesc(patronId, startDate, endDate, pageable)
                    .map(this::mapBookLostEntityToBookLostDto);
        }

        return bookLostReportRepository.findByBookBorrowing_Borrowing_Borrower_Id(patronId, pageable).map(this::mapBookLostEntityToBookLostDto);

    }
}
