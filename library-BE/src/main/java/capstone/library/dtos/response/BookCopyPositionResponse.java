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

    private int id;

    private String callNumber;

    private String line;

    private String shelf;

    public BookCopyPositionResponse(String callNumber, String line, String shelf) {
        this.callNumber = callNumber;
        this.line = line;
        this.shelf = shelf;
    }

    @Override
    public String toString() {
        return this.shelf + "-" + this.line;
    }
}
