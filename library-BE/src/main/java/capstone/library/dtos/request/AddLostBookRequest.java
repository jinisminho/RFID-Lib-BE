package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddLostBookRequest {

    private int bookBorrowingId;

    private double fine;

    private String reason;

    private int auditorId;
}
