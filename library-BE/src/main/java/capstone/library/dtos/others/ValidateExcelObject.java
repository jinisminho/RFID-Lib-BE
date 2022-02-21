package capstone.library.dtos.others;

import capstone.library.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateExcelObject {

    private boolean isValid;

    private String message;

    private List<Account> accountList;
}
