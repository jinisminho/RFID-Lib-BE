package capstone.library.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class UpdateBorrowPolicyRequest {

    @NotNull(message = "{borrowPolicy.id.notNull}")
    private Integer id;

    @NotNull(message = "{borrowPolicy.dueDuration.notNull}")
    private Integer dueDuration;

    @NotNull(message = "{borrowPolicy.maxNumberCopyBorrow.notNull}")
    private Integer maxNumberCopyBorrow;

    @NotNull(message = "{borrowPolicy.maxExtendTime.notNull}")
    private Integer maxExtendTime;

    @NotNull(message = "{borrowPolicy.extendDueDuration.notNull}")
    private Integer extendDueDuration;
}
