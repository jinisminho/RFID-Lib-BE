package capstone.library.dtos.response;

import capstone.library.entities.BookBorrowing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResDto {

    private Integer id;

    @NotNull(message = "{borrowing.borrowedAt.notNull}")
    private LocalDateTime borrowedAt;

    private String note;

    private ProfileAccountResDto borrower;

    private Set<BookBorrowing> bookBorrowings;

}
