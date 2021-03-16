package capstone.library.controllers.web;

import capstone.library.dtos.response.GenreResponseDto;
import capstone.library.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static capstone.library.util.constants.SecurityConstant.ADMIN;
import static capstone.library.util.constants.SecurityConstant.LIBRARIAN;

@RestController
@RequestMapping("/genre")
public class GenreController {
    @Autowired
    GenreService genreService;

    @GetMapping("/all")
    @Secured({ADMIN, LIBRARIAN})
    public List<GenreResponseDto> findAllGenres() {
        return genreService.findAllGenres();
    }
}
