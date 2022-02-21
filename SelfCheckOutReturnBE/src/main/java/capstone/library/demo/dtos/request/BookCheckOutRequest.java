package capstone.library.demo.dtos.request;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull(message = "{BookCheckOutRequest.body.notNull}")
public class BookCheckOutRequest {

    @NotNull(message = "{BookCheckOutRequest.patronId.notNull}")
    private int patronId;

    @NotNull(message = "{BookCheckOutRequest.bookCodeList.notNull}")
    private List<CheckOutBookRequest> bookCodeList;

}
