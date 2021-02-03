package capstone.library.demo.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull(message = "{CheckOutBookRequest.body.notNull}")
public class CheckOutBookRequest {

    @NotNull(message = "{CheckOutBookRequest.rfid.notNull}")
    @Length(max = 80 , message = "{CheckOutBookRequest.rfid.length}")
    private String rfid;

    @NotNull(message = "{CheckOutBookRequest.group.notNull}")
    private String group;

    private int groupId;
}
