package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutCopyDto
{
    private String rfid;

    private String title;

    private String subtitle;

    private String author;

    private String publisher;

    private int publishYear;

    private int edition;

    private boolean ableToBorrow;

    private String reason;

    private String dueDate;

    private String borrowedAt;
}
