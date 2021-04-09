package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static capstone.library.util.constants.PolicyConstant.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class PatronTypeReqDto {
    //    @NotNull(message = "{patronType.name.notNull}")
    private String name;

    //    @NotNull(message = "{patronType.maxBorrowNumber.notNull}")
    @Max(MAX_PATRON_NUMBER_BORROW)
    @Min(MIN_PATRON_NUMBER_BORROW)
    private Integer maxBorrowNumber;
}
