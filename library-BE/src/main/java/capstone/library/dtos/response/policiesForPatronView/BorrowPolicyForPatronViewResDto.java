package capstone.library.dtos.response.policiesForPatronView;

import capstone.library.dtos.common.BookCopyTypeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BorrowPolicyForPatronViewResDto {
    private int id;
    private Integer dueDuration;
    private Integer maxBorrowNumber;
    private Integer maxExtendTime;
    private Integer extendDueDuration;
    private BookCopyTypeDto bookCopyType;
}
