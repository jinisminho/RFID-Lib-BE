package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortableBookSearchPositionResponse {

    private String shelf;

    private String line;

    public String toString(){
        return this.shelf + "-" + this.line;
    }
}
