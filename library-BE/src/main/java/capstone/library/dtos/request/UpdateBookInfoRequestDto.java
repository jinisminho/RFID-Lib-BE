package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookInfoRequestDto
{
    @NotNull(message = "{id.notNull}")
    private int id;

    @Length(min = 1, max = 255, message = "{UpdateBookInfoRequestDto.tile.length}")
    private String title;

    @Length(min = 1, max = 255, message = "{UpdateBookInfoRequestDto.subtitle.length}")
    private String subtitle;

    private String callNumber;

    @Length(min = 1, max = 255, message = "{UpdateBookInfoRequestDto.publisher.length}")
    private String publisher;

    @Length(min = 1, max = 20, message = "{UpdateBookInfoRequestDto.language.length}")
    private String language;

    private Integer pageNumber;

    private Integer numberOfCopy;

    private Integer publishYear;

    private Integer edition;

    private String img;

    private List<Integer> genreIds;

    @Length(min = 1, max = 30, message = "{UpdateBookInfoRequestDto.status.length}")
    private String status;

    private List<Integer> authorIds;
}
