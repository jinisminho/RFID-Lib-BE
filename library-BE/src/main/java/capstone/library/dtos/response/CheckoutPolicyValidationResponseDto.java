package capstone.library.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutPolicyValidationResponseDto
{
    private boolean haveOverdueCopies;
    private boolean violatePolicy;
    private boolean duplicateBook;
    private List<String> reasons;

}
