package capstone.library.controllers.web;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.AddBookCopyTypeReqDto;
import capstone.library.dtos.request.BookCopyTypeReqDto;
import capstone.library.dtos.response.BookCopyTypeResponseDto;
import capstone.library.services.BookCopyTypeService;
import capstone.library.util.constants.ConstantUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.annotation.Secured;
import capstone.library.dtos.common.BookCopyTypeDto;

import static capstone.library.util.constants.SecurityConstant.*;

@RestController
@RequestMapping("/copyType")
public class BookCopyTypeController {
    @Autowired
    BookCopyTypeService bookCopyTypeService;

    @Secured({LIBRARIAN, ADMIN})
    @GetMapping("/all")
    public List<BookCopyTypeResponseDto> getCopiesList() {
        return bookCopyTypeService.getAllCopyTypes();
    }

    @Secured({LIBRARIAN, ADMIN})
    @ApiOperation(value = "This API get Book Copy Type for policy")
    @GetMapping("/getAll")
    public List<BookCopyTypeDto> getAllBookCopyType() {
        return bookCopyTypeService.getAllBookCopyType();
    }

    @ApiOperation(value = "This API create new book copy type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/add")
    public ResponseEntity<?> addBookCopyType(@RequestBody @Valid AddBookCopyTypeReqDto request) {

        boolean bool = bookCopyTypeService.addBookCopyType(request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to add book copy type");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "This API update book copy type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateBookCopyType(@NotNull @PathVariable int id, @RequestBody @Valid BookCopyTypeReqDto request) {

        boolean bool = bookCopyTypeService.updateBookCopyType(id, request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update book copy type");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "This API delete book copy type")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Missing input", response = ErrorDto.class)})
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookCopyType(@NotNull @PathVariable int id) {

        boolean bool = bookCopyTypeService.deleteBookCopyType(id);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update book copy type");

        return new ResponseEntity(bool ? ConstantUtil.DELETE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/find")
    public Page<BookCopyTypeDto> findBookCopyType(Pageable pageable,
                                                  @RequestParam(required = false, name = "name") String name) {
        return bookCopyTypeService.getPatronType(pageable, name);
    }
}