package capstone.library.controllers.web;

import capstone.library.dtos.response.AuthorResponseDto;
import capstone.library.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
