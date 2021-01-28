package capstone.library.dtos.common;

import capstone.library.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyBookDto
{
    private int id;

    private String img;

    private String isbn;

    private String title;

    private String subtitle;

    private String publisher;

    private int publishYear;

    private int edition;

    private String language;

    private int pageNumber;

    private String callNumber;

    private int numberOfCopy;

    private BookStatus status;

    private String authors;

    private String genres;

}
