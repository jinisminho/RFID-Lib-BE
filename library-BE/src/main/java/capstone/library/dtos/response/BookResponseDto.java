package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto
{
    private int bookCopyId;

    private String barcode;

    private String borrowedAt;

    private String dueAt;

    private int overdueDays;

    private String isbn;

    private String title;

    private String subtitle;

    private String publisher;

    private Integer publishYear;

    private Integer edition;

    private String language;

    private Integer pageNumber;

    private String callNumber;

    private String status;

    private String authors;

    private String genres;


//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    private static class BookDto
//    {
//
//    }
}
