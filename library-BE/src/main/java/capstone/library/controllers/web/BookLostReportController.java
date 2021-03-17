package capstone.library.controllers.web;

import capstone.library.dtos.request.ConfirmLostBookRequest;
import capstone.library.dtos.response.BookLostResponse;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.enums.LostBookStatus;
import capstone.library.services.BookLostReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static capstone.library.util.constants.ConstantUtil.DATE_TIME_PATTERN;
import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/lost")
public class BookLostReportController {

    @Autowired
    BookLostReportService bookLostReportService;

    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/getLostBookFine/{bookLostReportId}")
    public LostBookFineResponseDto getLostBookFine(@PathVariable(name = "bookLostReportId") @NotNull int bookLostReportId) {
        return bookLostReportService.getLostBookFine(bookLostReportId);
    }

    @ApiOperation(value = "This API use to get all lost books in a period or by status")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/find")
    public Page<BookLostResponse> findLostBooksInPeriod(Pageable pageable,
                                                        @RequestParam(required = false, name = "startDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime startDate,
                                                        @RequestParam(required = false, name = "endDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime endDate, @RequestParam(required = false, name = "status") LostBookStatus status) {
        if (status != null) {
            return bookLostReportService.findBookLostByStatus(status, startDate, endDate, pageable);
        } else {
            return bookLostReportService.findBookLostInPeriod(startDate, endDate, pageable);
        }
    }

    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/find/{patronId}")
    public Page<BookLostResponse> findLostBooksOfPatron(Pageable pageable,
                                                        @RequestParam(required = false, name = "startDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime startDate,
                                                        @RequestParam(required = false, name = "endDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime endDate,
                                                        @PathVariable("patronId") int patronId) {
        return bookLostReportService.findBookLostOfPatron(startDate, endDate, pageable, patronId);
    }

    @ApiOperation(value = "This API use to confirm pending book lost report and send email to patron")
    @Secured({LIBRARIAN, ADMIN})
    @PostMapping("/confirm")
    public String confirmBookLost(@RequestBody @NotNull ConfirmLostBookRequest request) {
        return bookLostReportService.confirmBookLost(request);
    }

    @ApiOperation(value = "This API allow  patron to report lost for:  overdue or borrowing book")
    @Secured({PATRON})
    @GetMapping("/reportByPatron/{bookBorrowingId}")
    public String reportBookLostByPatron(@PathVariable(name = "bookBorrowingId") int bookBorrowingId) {
        return bookLostReportService.reportLostByPatron(bookBorrowingId);
    }
}