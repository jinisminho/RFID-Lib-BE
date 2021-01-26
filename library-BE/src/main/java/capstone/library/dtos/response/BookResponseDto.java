package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto
{
    private String isbn;

    private String title;

    private String subtitle;

    private String publisher;

    private Integer publishYear;

    private Integer edition;

    private String language;

    private Integer pageNumber;

    private String ddc;

    private Integer numberOfCopy;

    private String status;

    private List<AuthorDto> author;

    private List<GenreDto> genre;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AuthorDto
    {
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class GenreDto
    {
        private String name;
    }
}
