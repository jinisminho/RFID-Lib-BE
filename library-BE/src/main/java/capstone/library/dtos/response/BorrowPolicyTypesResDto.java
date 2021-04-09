package capstone.library.dtos.response;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.common.PatronTypeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BorrowPolicyTypesResDto {
    List<PatronTypeDto> patronTypes;

    List<BookCopyTypeDto> bookCopyTypes;
}
