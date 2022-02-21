package capstone.library.dtos.email;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class EmailReturnBookDto {

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

    private String bookGroup;

    private String returnedAt;

    private String patronEmail;

    private String genres;

    private int edition;
}
