package capstone.library.controllers.web;

import capstone.library.dtos.response.BorrowingResDto;
import capstone.library.services.BorrowingService;
import capstone.library.util.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;

import static capstone.library.util.constants.ConstantUtil.DATE_TIME_PATTERN;
import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/borrowing")
public class BorrowingController {
    @Autowired
    BorrowingService borrowingService;


    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/get")
    @ApiPageable
    public Page<BorrowingResDto> getBorrowing(@ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable,
                                              @RequestParam(required = false, name = "from") @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime from,
                                              @RequestParam(required = false, name = "to") @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime to) {
        return borrowingService.findAll(from, to, pageable);
    }
}
