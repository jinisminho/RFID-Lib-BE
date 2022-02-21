package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ValidateRenewDto {
    List<String> reasons;
    boolean violatePolicy;
    boolean ableToRenew;
    LocalDate newDueDate;
}
