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
@NotNull(message = "{ReturnOneRequest.body.notNull}")
public class ReturnOneRequest {

    @NotNull(message = "{ReturnOneRequest.bookRfid.notNull}")
    @Length(max = 80, message = "{ReturnOneRequest.bookRfid.length}")
    private String bookRfid;
}
