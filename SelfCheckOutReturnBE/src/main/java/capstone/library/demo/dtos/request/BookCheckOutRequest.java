package capstone.library.demo.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class BookCheckOutRequest {

    @NotNull(message = "{BookCheckOutRequest.patronId.notNull}")
    private int patronId;

    private List<String> bookCodeList;


}
