package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    private int accountId;

    @NotNull(message = "{account.currentPass.notNull}")
    private String currentPassword;

    @NotNull(message = "{account.newPass.notNull}")
    @Length(min = 6, max = 50)
    private String newPassword;
}
