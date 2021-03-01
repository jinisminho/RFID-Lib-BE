package capstone.library.dtos.request;

import capstone.library.enums.BookStatus;
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
public class UpdateBookInfoRequestDto implements Serializable {
    @NotNull(message = "{id.notNull}")
    private int id;

    @NotNull(message = "{isbn.notNull}")
    private String iSBN;

    @Length(min = 1, max = 255, message = "{UpdateBookInfoRequestDto.tile.length}")
    private String title;

    private String subtitle;

    private String callNumber;

    @Length(min = 1, max = 255, message = "{UpdateBookInfoRequestDto.publisher.length}")
    private String publisher;

    @Length(min = 1, max = 20, message = "{UpdateBookInfoRequestDto.language.length}")
    private String language;

    private Integer pageNumber;

    private Integer publishYear;

    private Integer edition;

    private String img;

    private List<Integer> genreIds;

    private BookStatus status;

    private List<Integer> authorIds;

    private int updateBy;

}
