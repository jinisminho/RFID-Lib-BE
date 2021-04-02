package capstone.library.demo.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCheckOutResponse {

    private String rfid;

    private String title;

    private boolean ableToBorrow;

    private String dueDate;

    private Integer edition;

    private String authors;

    private  String img;

    private String subtitle;

    private String group;

    private String borrowedAt;

    private  String genres;

    private int feePolicyId = 0;

}
