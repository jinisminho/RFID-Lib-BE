package capstone.library.controllers.web;

import capstone.library.dtos.request.SaveSamplePositionRequestDto;
import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookCopyPosition;
import capstone.library.services.BookCopyPositionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

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
}
