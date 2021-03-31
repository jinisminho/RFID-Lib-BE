package capstone.library.demo.dtos.response;

import capstone.library.demo.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScannedReturnBookResponse {

    private String rfid ;

    private String title;

    private int edition;

    private String authors;

    private String img;

    private String subtitle;

    private int groupId;

    private String group;

    private  String genres;

    private int bookId;

    private String borrower;

    private BookCopyStatus status;

}
