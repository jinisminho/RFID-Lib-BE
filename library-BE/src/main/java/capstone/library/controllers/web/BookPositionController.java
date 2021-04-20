package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.CreateCopyPostionReqDto;
import capstone.library.dtos.request.SaveSamplePositionRequestDto;
import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookCopyPosition;
import capstone.library.services.BookCopyPositionService;
import capstone.library.util.ApiPageable;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/position")
public class BookPositionController {

    @Autowired
    BookCopyPositionService bookCopyPositionService;

    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/copy")
    public BookCopyPositionResponse findBookCopyPosition(@RequestParam(name = "bookCopyId") int bookCopyId) {
        return bookCopyPositionService.findPositionForBookCopy(bookCopyId);
    }

    @Secured({LIBRARIAN, ADMIN, PATRON})
    @GetMapping("/book")
    public List<BookCopyPositionResponse> findBookPosition(@RequestParam(name = "bookId") int bookId) {
        return bookCopyPositionService.findPositionForBook(bookId);
    }

    @ApiOperation("Get a list of all shelves")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/shelf/all")
    public Set<String> getAllShelves() {
        return bookCopyPositionService.getAllShelves();
    }

    @ApiOperation("Get a list of row on a shelf")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/row/{shelf}")
    public List<BookCopyPosition> getRowByShelf(@PathVariable String shelf) {
        return bookCopyPositionService.getRowByShelf(shelf);
    }

    /*Save a list of book copies (RFID) to a position (shelf + row)*/
    @ApiOperation("Save sample position. Save a list of book copies (RFID) to a position (shelf + row)")
    @Secured({LIBRARIAN, ADMIN})
    @PostMapping("/row/save")
    public String saveSampledPosition(@RequestBody SaveSamplePositionRequestDto request) {
        return bookCopyPositionService.saveSampledPosition(request);
    }

    @ApiOperation("Get available/lib use only books on a row of a shelf")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/row/getBooks/{positionId}")
    public List<CopyResponseDto> getBooksOnARow(@PathVariable int positionId) {
        return bookCopyPositionService.getBooksOnARow(positionId);
    }

    @ApiOperation("Get available/lib use only books on a row of a shelf with rfid")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/rfid/getBooks/{rfid}")
    public List<CopyResponseDto> getBooksOnARow(@PathVariable String rfid) {
        return bookCopyPositionService.getBooksOnARowByRFID(rfid);
    }

    @ApiOperation("Get a position (shelf + row) by RFID")
    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/rfid/getPosition/{rfid}")
    public BookCopyPositionResponse getPositionByRFID(@PathVariable String rfid) {
        return bookCopyPositionService.getPositionByRFID(rfid);
    }

    @PostMapping("/create")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> createPos(@RequestBody @Valid CreateCopyPostionReqDto request) {
        boolean bool = bookCopyPositionService.addPos(request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to created new position");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> addPos(@RequestParam(required = true, value = "posId") Integer posId, @RequestBody CreateCopyPostionReqDto request) {
        boolean bool = bookCopyPositionService.updatePos(posId, request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update position");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> deletePos(@RequestParam(required = true, value = "posId") Integer posId) {
        boolean bool = bookCopyPositionService.deletePos(posId);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to delete position");

        return new ResponseEntity(bool ? ConstantUtil.DELETE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation("Get a page of all position (shelf + line)")
    @Secured({LIBRARIAN, ADMIN})
    @ApiPageable
    @GetMapping("/all")
    public Page<BookCopyPositionResponse> getAll(@RequestParam(required = false, value = "shelf") String shelf, @RequestParam(required = false, value = "line") Integer line, @ApiIgnore("Ignored because swagger ui shows the wrong params") Pageable pageable) {
        return bookCopyPositionService.getAll(shelf, line, pageable);
    }
}
