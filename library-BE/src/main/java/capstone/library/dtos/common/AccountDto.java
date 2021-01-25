package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Integer id;

    @NotNull(message = "{account.email.notNull}")
    @Length(max = 100, message = "{account.email.length}")
    private String email;

    @NotNull(message = "{account.password.notNull}")
    @Length(max = 100, message = "{account.password.length}")
    private String password;

    @Length(max = 80, message = "{account.rfid.length}")
    private String rfid;

    @Length(max = 500, message = "{account.avatar.length}")
    private String avatar;

    @NotNull(message = "{account.isActive.notNull}")
    private boolean isActive;

    private AccountDto creator;

    private AccountDto updater;

    private RoleDto role;

    private ProfileDto profile;

}
