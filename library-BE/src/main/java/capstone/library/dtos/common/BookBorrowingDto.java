package capstone.library.dtos.common;

import capstone.library.dtos.response.CopyResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookBorrowingDto {

    private Integer id;

    private LocalDateTime returnedAt;

    private LocalDate dueAt;

    private LocalDateTime extendedAt;

    private int extendIndex;

    private LocalDateTime lostAt;

    private int issuedBy;

    private int returnBy;

    private CopyResponseDto bookCopy;

    private Double fine;

    private int feePolicyId;

    private int borrowingId;
}
