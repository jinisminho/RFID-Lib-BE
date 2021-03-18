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

    private String callNumber;

    private String line;

    private String shelf;

    @Override
    public String toString() {
        return this.shelf + "-" + this.line;
    }
}
