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
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class UpdateBorrowPolicyRequest {

    @NotNull(message = "{borrowPolicy.id.notNull}")
    private Integer id;

    @NotNull(message = "{borrowPolicy.dueDuration.notNull}")
    @Max(MAX_DUE_DURATION)
    @Min(MIN_DUE_DURATION)
    private Integer dueDuration;

    @NotNull(message = "{borrowPolicy.maxNumberCopyBorrow.notNull}")
    @Max(MAX_NUMBER_BORROW)
    @Min(MIN_NUMBER_BORROW)
    private Integer maxNumberCopyBorrow;

    @NotNull(message = "{borrowPolicy.maxExtendTime.notNull}")
    @Max(MAX_EXTEND_TIME)
    @Min(MIN_EXTEND_TIME)
    private Integer maxExtendTime;

    @NotNull(message = "{borrowPolicy.extendDueDuration.notNull}")
    @Max(MAX_EXTEND_DUE_DURATION)
    @Min(MIN_EXTEND_DUE_DURATION)
    private Integer extendDueDuration;
}
