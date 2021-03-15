package capstone.library.services.impl;

import capstone.library.dtos.common.BookBorrowingDto;
import capstone.library.dtos.request.ConfirmLostBookRequest;
import capstone.library.dtos.response.BookLostResponse;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookLostReport;
import capstone.library.entities.FeePolicy;
import capstone.library.enums.LostBookStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.BookLostReportRepository;
import capstone.library.repositories.FeePolicyRepository;
import capstone.library.services.BookLostReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

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
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BOOK_BORROWING_NOT_FOUND_ERROR = "Cannot find this borrowing section";

    @Override
    public LostBookFineResponseDto getLostBookFine(int bookBorrowingId) {
        BookBorrowing bookBorrowing = bookBorrowingRepository.findById(bookBorrowingId).
                orElseThrow(() -> new ResourceNotFoundException("Book borrowing", BOOK_BORROWING_NOT_FOUND_ERROR));

        LostBookFineResponseDto response = new LostBookFineResponseDto();
        response.setBookBorrowingInfo(objectMapper.convertValue(bookBorrowing, BookBorrowingDto.class));
        response.setLostBookFineInMarket(calculateLostFineInMarket(bookBorrowing));
        response.setLostBookFineNotInMarket(calculateLostFineNotInMarket(bookBorrowing));
        return response;
    }

    @Override
    @Transactional
    public String confirmBookLost(ConfirmLostBookRequest lostBook) {
        if(lostBook == null){
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

       //email to patron

       return UPDATE_SUCCESS;
    }

    //when patron request lost book
    @Override
    public String reportLostByPatron(int bookBorrowingId) {
        BookBorrowing borrowing = bookBorrowingRepository
                .findById(bookBorrowingId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Borrowing",
                        "Cannot find book borrowing with id: "+ bookBorrowingId));

        LocalDateTime lostAt = LocalDateTime.now();
        BookLostReport bookLostReport = new BookLostReport();
        bookLostReport.setLostAt(lostAt);
        bookLostReport.setFine(0.0);
        bookLostReport.setBookBorrowing(borrowing);
        bookLostReport.setStatus(LostBookStatus.PENDING);
        bookLostReportRepository.save(bookLostReport);
        borrowing.setLostAt(lostAt);
        bookBorrowingRepository.save(borrowing);
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
                .findByStatusAndLostAtBetweenOrderByLostAtDesc(status,startDate, endDate, pageable)
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
        return dto;
    }

    private double calculateLostFineInMarket(BookBorrowing bookBorrowing) {
        //Get the latest fee policy. latest fee policy is the first item in the list
        List<FeePolicy> feePolicies = feePolicyRepository.findAllByOrderByCreatedAtDesc();
        return bookBorrowing.getBookCopy().getPrice() + feePolicies.get(0).getDocumentProcessing_Fee();
    }

    private double calculateLostFineNotInMarket(BookBorrowing bookBorrowing) {
        //Get the latest fee policy. latest fee policy is the first item in the list
        List<FeePolicy> feePolicies = feePolicyRepository.findAllByOrderByCreatedAtDesc();
        return bookBorrowing.getBookCopy().getPrice() * feePolicies.get(0).getMissingDocMultiplier();
    }
}
