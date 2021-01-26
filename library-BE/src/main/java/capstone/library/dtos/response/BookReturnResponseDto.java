package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookReturnResponseDto
{
    private String rfid;

    private String title;

    private String subtitle;

    private boolean overdue;

    private int overdueDays;

    private String reason;

    private String dueDate;

    private double fine;

    private double bookPrice;
}
