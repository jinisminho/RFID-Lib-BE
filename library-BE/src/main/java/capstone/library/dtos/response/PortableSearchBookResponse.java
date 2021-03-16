package capstone.library.dtos.response;

import capstone.library.dtos.common.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortableSearchBookResponse {

    private int id;

    private String isbn;

    private String title;

    private String subtitle;

    private String publisher;

    private Integer publishYear;

    private Integer edition;

    private String callNumber;

    private Integer numberOfCopy;

    private String status;

    private String authors;

    private List<BookCopyPositionResponse> positionList;

    private List<String> CopyRfidList;
}
