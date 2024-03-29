package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResDto {

    private int id;

    @NotNull(message = "{book.isbn.notNull}")
    private String isbn;

    @NotNull(message = "{book.subtitle.notNull}")
    private String title;

    private String subtitle;

    @NotNull(message = "{book.publisher.notNull}")
    private String publisher;

    @NotNull(message = "{book.publishYear.notNull}")
    private Integer publishYear;

    @NotNull(message = "{book.edition.notNull}")
    private Integer edition;

    @NotNull(message = "{book.language.notNull}")
    private String language;

    @NotNull(message = "{book.pageNumber.notNull}")
    private Integer pageNumber;

    @NotNull(message = "{book.callNumber.notNull}")
    private String callNumber;

    @NotNull(message = "{book.numberOfCopy.notNull}")
    private Integer numberOfCopy;

    @NotNull(message = "{book.status.notNull}")
    private String status;

    public Set<AuthorResDto> author;

    private String authorsString;

//    public Set<GenreResDto> genres;

//    private String genresString;

    private String genre;

    private String img;

    private Integer stock;

    private boolean available;

    private boolean onlyInLibrary;
}
