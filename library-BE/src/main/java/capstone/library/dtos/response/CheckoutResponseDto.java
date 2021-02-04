package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDto implements Serializable
{
    private String rfid;

    private String title;

    private String subtitle;

    private String author;

    private boolean ableToBorrow;

    private String reason;

    private String dueDate;

}
