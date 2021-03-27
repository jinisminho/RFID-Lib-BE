package capstone.library.dtos.response.policiesForPatronView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeePolicyForPatronViewResDto {

    private Integer id;

    private Double overdueFinePerDay;

    private Integer maxPercentageOverdue;

    private Double documentProcessingFee;

    private Integer missingDocMultiplier;

}
