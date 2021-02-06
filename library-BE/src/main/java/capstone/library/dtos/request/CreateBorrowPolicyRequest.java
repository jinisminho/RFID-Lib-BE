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
public class CreateBorrowPolicyRequest {

    @NotNull(message = "{borrowPolicy.dueDuration.notNull}")
    private Integer dueDuration;

    @NotNull(message = "{borrowPolicy.maxNumberCopyBorrow.notNull}")
    private Integer maxNumberCopyBorrow;

    @NotNull(message = "{borrowPolicy.maxExtendTime.notNull}")
    private Integer maxExtendTime;

    @NotNull(message = "{borrowPolicy.extendDueDuration.notNull}")
    private Integer extendDueDuration;

    @NotNull(message = "{borrowPolicy.patronTypeId.notNull}")
    private Integer patronTypeId;

    @NotNull(message = "{borrowPolicy.bookCopyTypeId.notNull}")
    private Integer bookCopyTypeId;

}
