package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ScannedRFIDCopiesRequestDto implements Serializable
{
    private int patronId;

    @NotNull(message = "{BookCheckoutRequestDto.patronId.notNull}")
    private int librarianId;

    private List<String> bookRfidTags;
}
