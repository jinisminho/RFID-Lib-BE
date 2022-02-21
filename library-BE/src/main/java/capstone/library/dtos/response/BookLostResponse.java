package capstone.library.dtos.response;

import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import capstone.library.entities.BookCopy;
import capstone.library.enums.LostBookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookLostResponse {

    private Integer id;

    private LocalDateTime lostAt;

    private LocalDateTime borrowedAt;

    private Double fine;

    private String reason;

    private String borrowerEmail;

    private String barcode;

    private String title;

    private String subtitle;

    private Integer edition;

    private String ISBN;

    private LostBookStatus status;
}
