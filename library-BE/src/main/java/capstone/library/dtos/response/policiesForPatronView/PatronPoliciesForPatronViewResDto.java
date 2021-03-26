package capstone.library.dtos.response.policiesForPatronView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PatronPoliciesForPatronViewResDto {
    private List<PatronPolicyForPatronViewResDto> data;
    private Integer count;
}
