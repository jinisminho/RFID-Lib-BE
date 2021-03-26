package capstone.library.dtos.response.policiesForPatronView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PatronPolicyForPatronViewResDto {
    private int id;
    private String name;
    private Integer maxBorrowNumber;
    private List<BorrowPolicyForPatronViewResDto> borrowPolicies;
}
