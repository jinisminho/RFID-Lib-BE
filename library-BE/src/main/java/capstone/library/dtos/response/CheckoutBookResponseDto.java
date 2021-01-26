package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutBookResponseDto
{
    private String rfid;

    private String title;

    private String subtitle;

    private String author;

    private boolean ableToBorrow;

    private String reason;

    private String dueDate;

}
