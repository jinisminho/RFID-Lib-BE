package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyPositionResponse {

    private String bookCopyType;

    private String callNumber;

    private String line;

    private String shelf;

    private String status;

}
