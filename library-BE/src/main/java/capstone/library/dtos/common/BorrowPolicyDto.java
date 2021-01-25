package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowPolicyDto {

    private Integer id;

    @NotNull(message = "{borrowPolicy.dueDuration.notNull}")
    private int dueDuration;

    @NotNull(message = "{borrowPolicy.maxNumberCopyBorrow.notNull}")
    private int maxNumberCopyBorrow;

    @NotNull(message = "{borrowPolicy.maxExtendTime.notNull}")
    private int maxExtendTime;

    @NotNull(message = "{borrowPolicy.extendDueDuration.notNull}")
    private int extendDueDuration;

    @NotNull(message = "{borrowPolicy.overdueFinePerDay.notNull}")
    private double overdueFinePerDay;

    private String policyFormUrl;

}
