package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBookResponseDto
{
    private String rfid;

    private String title;

    private String subtitle;

    private String authors;

    private String isbn;

    private boolean overdue;

    private int overdueDays;

    private String reason;

    private String dueDate;

    private double fine;

    private double bookPrice;
}