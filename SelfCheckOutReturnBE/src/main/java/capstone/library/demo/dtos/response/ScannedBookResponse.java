package capstone.library.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScannedBookResponse {

    private String rfid ;

    private String title;

    private int edition;

    private String authors;

    private String img;

    private String subtitle;

    private int groupId;

    private String group;

}
