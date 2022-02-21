package capstone.library.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ScannedRFIDCopiesRequestDto implements Serializable {
    private int patronId;

    @NotNull(message = "{BookCheckoutRequestDto.patronId.notNull}")
    private int librarianId;

    @JsonIgnore
    private boolean checkin;

    @Length(max = 500, message = "{ScannedRFIDCopiesRequestDto.maxLength.checkoutNote}")
    private String checkoutNote;

    private List<String> bookRfidTags;
}
