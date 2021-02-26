package capstone.library.services.impl;

import capstone.library.dtos.response.BookLostResponse;
import capstone.library.entities.BookLostReport;
import capstone.library.exceptions.MissingInputException;
import capstone.library.repositories.BookLostReportRepository;
import capstone.library.services.BookLostReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookLostReportServiceImpl implements BookLostReportService {

    @Autowired
    private BookLostReportRepository bookLostReportRepo;

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
        return bookLostReportRepo
                .findByLostAtBetweenOrderByLostAtDesc(startDate, endDate, pageable)
                .map(this::mapBookLostEntityToBookLostDto);
    }

    private BookLostResponse mapBookLostEntityToBookLostDto(BookLostReport entity){
        BookLostResponse dto = new  BookLostResponse();
        dto.setId(entity.getId());
        dto.setLostAt(entity.getLostAt());
        dto.setBorrowedAt(entity.getBookBorrowing().getBorrowing().getBorrowedAt());
        dto.setFine(entity.getFine());
        dto.setReason(entity.getReason());
        dto.setBorrowerEmail(entity.getBorrower().getEmail());
        dto.setBarcode(entity.getBookBorrowing().getBookCopy().getBarcode());
        dto.setTitle(entity.getBookBorrowing().getBookCopy().getBook().getTitle());
        dto.setSubtitle(entity.getBookBorrowing().getBookCopy().getBook().getSubtitle());
        dto.setEdition(entity.getBookBorrowing().getBookCopy().getBook().getEdition());
        dto.setISBN(entity.getBookBorrowing().getBookCopy().getBook().getIsbn());
        return dto;
    }
}
