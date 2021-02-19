package capstone.library.dtos.response;

import capstone.library.dtos.common.FeePolicyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowingResDto {

    private Integer id;

    private LocalDateTime returnedAt;

    @NotNull(message = "{bookBorrowing.dueAt.notNull}")
    private LocalDate dueAt;

    private LocalDateTime extendedAt;

    private Integer extendIndex;

    private ProfileAccountResDto issued_by;

    private ProfileAccountResDto return_by;

    private BookCopyResDto bookCopy;

    private BorrowingResDto borrowing;

    private FeePolicyDto feePolicy;

    private Integer overdueDays;

    private Double fine;

}
