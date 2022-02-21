package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCopyPostionReqDto implements Serializable {
    @NotNull(message = "{author.line.notNull}")
    private Integer line;

    @NotNull(message = "{author.shelf.notNull}")
    @Length(max = 100, message = "{author.shelf.length}")
    private String shelf;

    @NotNull(message = "{author.rfid.notNull}")
    @Length(max = 80, message = "{author.rfid.length}")
    private String rfid;
}
