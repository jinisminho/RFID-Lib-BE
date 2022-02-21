package capstone.library.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static capstone.library.util.constants.PolicyConstant.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddFeePolicyRequest {

    @NotNull(message = "{feePolicy.id.notNull}")
    @Max(MAX_FINE_PER_DAY)
    @Min(MIN_FINE_PER_DAY)
    private Double overdueFinePerDay;

    @NotNull(message = "{feePolicy.id.notNull}")
    @Max(MAX_PERCENTAGE_OVERDUE_FINE)
    @Min(MIN_PERCENTAGE_OVERDUE_FINE)
    private Integer maxPercentageOverdueFine;

    @NotNull(message = "{feePolicy.id.notNull}")
    @Max(MAX_DOC_PROCESSING_FEE)
    @Min(MIN_DOC_PROCESSING_FEE)
    private Double documentProcessing_Fee;

    @NotNull(message = "{feePolicy.id.notNull}")
    @Max(MAX_DOC_MULTIPLIER)
    @Min(MIN_DOC_MULTIPLIER)
    private Integer missingDocMultiplier;
}
