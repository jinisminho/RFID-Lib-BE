package capstone.library.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class UpdatePatronTypePolicyRequest {

    @NotNull(message = "patronType.id.notNull")
    private Integer id;

    @NotNull(message = "patronType.maxBorrowNumber.notNull")
    private Integer maxBorrowNumber;
}
