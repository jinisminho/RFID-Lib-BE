package capstone.library.util.tools;

import capstone.library.dtos.response.BookResponseDto;
import capstone.library.entities.BookBorrowing;
import capstone.library.repositories.BookBorrowingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Service
public class OverdueBooksFinder
{
    @Autowired
    BookBorrowingRepository bookBorrowingRepository;
    @Autowired
    ObjectMapper objectMapper;

    public List<BookResponseDto> findOverdueBooksByPatronId(int id)
    {
        List<BookResponseDto> response = new ArrayList<>();
        List<BookBorrowing> allBorrowingBooks = bookBorrowingRepository.findByBorrowerIdAndReturnedAtIsNullAndLostAtIsNull(id);
        for (BookBorrowing bookBorrowing : allBorrowingBooks)
        {
            int overdueDays = LocalDate.now().compareTo(bookBorrowing.getDueAt());
            if (overdueDays > 0)
            {
                BookResponseDto dto = objectMapper.convertValue(bookBorrowing.getBookCopy().getBook(), BookResponseDto.class);
                dto.setAuthors(bookBorrowing.getBookCopy().getBook().getBookAuthors().
                        toString().replace("]", "").replace("[", ""));
                dto.setGenres(bookBorrowing.getBookCopy().getBook().getBookGenres().
                        toString().replace("]", "").replace("[", ""));
                dto.setBarcode(bookBorrowing.getBookCopy().getBarcode());
                dto.setBorrowedAt(bookBorrowing.getBorrowedAt().toString());
                dto.setDueAt(bookBorrowing.getDueAt().toString());
                dto.setOverdueDays(overdueDays);
                dto.setBookCopyId(bookBorrowing.getBookCopy().getId());
                response.add(dto);
            }
        }
        return response;
    }
}
