package capstone.library.demo.dtos.response;

import capstone.library.demo.enums.BookReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookReturnResponse {

    private String rfid;

    private String title;

    private BookReturnStatus status;

}
