package capstone.library.dtos.response.policiesForPatronView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoliciesForPatronViewResDto {
    private int id;
    private FeePolicyForPatronViewResDto feePolicy;
    private PatronPoliciesForPatronViewResDto patronTypes;
}
