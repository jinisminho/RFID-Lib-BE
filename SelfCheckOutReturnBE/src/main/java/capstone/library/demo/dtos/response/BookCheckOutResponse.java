package capstone.library.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCheckOutResponse {

    private String rfid;

    private String title;

    private boolean ableToBorrow;

    private String dueDate;

}
