package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.services.BookService;
import capstone.library.util.ApiPageable;
import capstone.library.util.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @ApiPageable
    @GetMapping("/search")
    public Page<BookResDto> findBooks(@RequestParam(required = false, value = "searchValue") String searchValue, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return bookService.findBooks(searchValue, pageable);
    }

    @ApiOperation(value = "This API add/update RFID tag to bookCopy by bookCopyId")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/bookCopy/tagRfid/{bookCopyId}")
    public ResponseEntity<?> tagRfidToBookCopy(@PathVariable Integer bookCopyId,
                                               @RequestParam(required = false, value = "rfid") String rfid) {

        boolean bool = bookService.tagRfidToBookCopy(bookCopyId, rfid);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL SERVER ERROR",
                "Failed to tag a rfid to bookCopy");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/reindex")
    public ResponseEntity<?> reindexAll() {
        boolean bool = bookService.reindexAll();
        return new ResponseEntity(bool ? "Reindexed" : "Failed to reindex", bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
