package capstone.library.controllers.web;

import capstone.library.dtos.response.AuthorResponseDto;
import capstone.library.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/author")
public class AuthorController
{
    @Autowired
    AuthorService authorService;

    @GetMapping("/all")
    public List<AuthorResponseDto> findAllBooks()
    {
        return authorService.findAllAuthors();
    }

}
