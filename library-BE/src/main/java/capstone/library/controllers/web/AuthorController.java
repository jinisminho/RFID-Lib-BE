package capstone.library.controllers.web;

import capstone.library.dtos.common.ErrorDto;
import capstone.library.dtos.request.CreateAuthorReqDto;
import capstone.library.dtos.response.AuthorResponseDto;
import capstone.library.services.AuthorService;
import capstone.library.util.constants.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/author")
public class AuthorController {
    @Autowired
    AuthorService authorService;

    @Secured({ADMIN, LIBRARIAN})
    @GetMapping("/all")
    public List<AuthorResponseDto> findAllBooks() {
        return authorService.findAllAuthors();
    }

    @PostMapping("/create")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> createAuthor(@RequestBody @Valid CreateAuthorReqDto request) {
        boolean bool = authorService.addAuthor(request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to created new author");

        return new ResponseEntity(bool ? ConstantUtil.CREATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> addAuthor(@RequestParam(required = true, value = "authorId") Integer authorId, @RequestBody CreateAuthorReqDto request) {
        boolean bool = authorService.updateAuthor(authorId, request);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to update author");

        return new ResponseEntity(bool ? ConstantUtil.UPDATE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete")
    @Secured({ADMIN, LIBRARIAN})
    public ResponseEntity<Resource> deleteAuthor(@RequestParam(required = true, value = "authorId") Integer authorId) {
        boolean bool = authorService.deleteAuthor(authorId);

        ErrorDto error = new ErrorDto(LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                "Failed to delete author");

        return new ResponseEntity(bool ? ConstantUtil.DELETE_SUCCESS : error,
                bool ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
