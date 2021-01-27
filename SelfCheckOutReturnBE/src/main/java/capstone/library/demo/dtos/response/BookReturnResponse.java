package capstone.library.demo.dtos.response;

import capstone.library.demo.enums.BookReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookReturnResponse {

    private String rfid;

    private String title;

    private BookReturnStatus status;

    private Integer edition;

    private String authors;

    private  String img;

    private String subtitle;

    private String group;

    private int overdueDay = 0;

    private double fine = 0;

    private String returnedAt;

    private String genres;

    private String patron;


}
