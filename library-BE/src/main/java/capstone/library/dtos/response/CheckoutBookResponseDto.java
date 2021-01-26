package capstone.library.dtos.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Checkout response")
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
