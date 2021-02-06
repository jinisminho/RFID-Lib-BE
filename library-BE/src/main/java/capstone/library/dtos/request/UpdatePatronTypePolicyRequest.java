package capstone.library.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static capstone.library.util.constants.PolicyConstant.MAX_NUMBER_BORROW;
import static capstone.library.util.constants.PolicyConstant.MIN_NUMBER_BORROW;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class UpdatePatronTypePolicyRequest {

    @NotNull(message = "{patronType.id.notNull}")
    private Integer id;

    @NotNull(message = "{patronType.maxBorrowNumber.notNull}")
    @Max(MAX_NUMBER_BORROW)
    @Min(MIN_NUMBER_BORROW)
    private Integer maxBorrowNumber;
}
