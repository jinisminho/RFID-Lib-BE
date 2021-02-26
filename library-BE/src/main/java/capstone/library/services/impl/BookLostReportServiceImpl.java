package capstone.library.services.impl;

import capstone.library.dtos.common.BookBorrowingDto;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.FeePolicy;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookBorrowingRepository;
import capstone.library.repositories.FeePolicyRepository;
import capstone.library.services.BookLostReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookLostReportServiceImpl implements BookLostReportService {
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    FeePolicyRepository feePolicyRepository;
    @Autowired
    ObjectMapper objectMapper;

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
