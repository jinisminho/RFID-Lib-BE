package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowingsResDto {

    Page<BookBorrowingResDto> overdued;

    Page<BookBorrowingResDto> borrowing;
    
    Page<BookBorrowingResDto> returned;
}
