package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequestDto implements Serializable {
    @NotNull(message = "{AddBookRequestDto.isbn.notNull}")
    @Length(min = 10, max = 13, message = "{AddBookRequestDto.isbn.length}")
    private String isbn;
    @NotNull(message = "{AddBookRequestDto.title.notNull}")
    @Length(min = 1, max = 255, message = "{AddBookRequestDto.tile.length}")
    private String title;
    @Length(min = 1, max = 255, message = "{AddBookRequestDto.subtitle.length}")
    private String subtitle;
    @NotNull(message = "{AddBookRequestDto.callNumber.notNull}")
//    @Length(min = 1, max = 255, message = "{AddBookRequestDto.callNumber.length}")
    private double ddc;
    @NotNull(message = "{AddBookRequestDto.publisher.notNull}")
    @Length(min = 1, max = 255, message = "{AddBookRequestDto.publisher.length}")
    private String publisher;
    @NotNull(message = "{AddBookRequestDto.language.notNull}")
    @Length(min = 1, max = 20, message = "{AddBookRequestDto.language.length}")
    private String language;
    @NotNull(message = "{AddBookRequestDto.page.notNull}")
    private int pageNumber;
    @NotNull(message = "{AddBookRequestDto.numberOfCopy.notNull}")
    private int numberOfCopy;
    @NotNull(message = "{AddBookRequestDto.publishYear.notNull}")
    private int publishYear;
    @NotNull(message = "{AddBookRequestDto.edition.notNull}")
    private int edition;
    @NotNull(message = "{AddBookRequestDto.genres.notNull}")
    private List<Integer> genreIds;
    @NotNull(message = "{AddBookRequestDto.status.notNull}")
    @Length(min = 1, max = 30, message = "{AddBookRequestDto.status.length}")
    private String status;
    @NotNull(message = "{AddBookRequestDto.authors.notNull}")
    private List<Integer> authorIds;
    @NotNull(message = "{AddBookRequestDto.creator.notNull}")
    private int creatorId;
    @Length(min = 1, max = 500, message = "{AddBookRequestDto.img.length}")
    private String img;
}
