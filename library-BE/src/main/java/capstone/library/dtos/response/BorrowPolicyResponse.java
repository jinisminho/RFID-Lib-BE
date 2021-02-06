package capstone.library.dtos.response;


import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.common.PatronTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowPolicyResponse {

    private Integer id;

    private int dueDuration;

    private int maxNumberCopyBorrow;

    private int maxExtendTime;

    private int extendDueDuration;

    private PatronTypeDto patronType;

    private BookCopyTypeDto bookCopyType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
