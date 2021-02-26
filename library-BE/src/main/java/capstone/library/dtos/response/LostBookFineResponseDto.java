package capstone.library.dtos.response;

import capstone.library.dtos.common.BookBorrowingDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LostBookFineResponseDto {
    BookBorrowingDto bookBorrowingInfo;
    double lostBookFineInMarket;
    double lostBookFineNotInMarket;
}
