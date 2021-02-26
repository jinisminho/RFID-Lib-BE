package capstone.library.controllers.web;

import capstone.library.dtos.request.AddLostBookRequest;
import capstone.library.dtos.response.BookLostResponse;
import capstone.library.dtos.response.LostBookFineResponseDto;
import capstone.library.services.BookLostReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static capstone.library.util.constants.ConstantUtil.DATE_TIME_PATTERN;
@RestController
@RequestMapping("/lost")
public class BookLostReportController {

    @Autowired
    BookLostReportService bookLostReportService;

    @GetMapping("/getLostBookFine/{bookBorrowingId}")
    public LostBookFineResponseDto getLostBookFine(@PathVariable @NotNull int bookBorrowingId) {
        return bookLostReportService.getLostBookFine(bookBorrowingId);
    }
    @GetMapping("/find")
    public Page<BookLostResponse> findLostBooksInPeriod(Pageable pageable,
                                                        @RequestParam(required = false, name = "startDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime startDate,
                                                        @RequestParam(required = false, name = "endDate")
                                                        @DateTimeFormat(pattern = DATE_TIME_PATTERN)
                                                                LocalDateTime endDate) {
        return bookLostReportService.findBookLostInPeriod(startDate, endDate, pageable);
    }

    @PostMapping("/add")
    public String addLostBook(@RequestBody @NotNull AddLostBookRequest request){
        return bookLostReportService.addLostBook(request);
    }
}