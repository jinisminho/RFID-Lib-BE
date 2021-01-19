package capstone.library.dtos.response;

import capstone.library.dtos.common.RoleDto;
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
public class ProfileAccountResDto {

    private Integer id;

    @NotNull(message = "{profileAccount.email.notNull}")
    @Length(max = 100, message = "{profileAccount.email.length}")
    private String email;

    @NotNull(message = "{profileAccount.password.notNull}")
    @Length(max = 100, message = "{profileAccount.password.length}")
    private String password;

    @Length(max = 4, message = "{profileAccount.pin.length}")
    private String pin;

    @Length(max = 80, message = "{profileAccount.rfid.length}")
    private String rfid;

    @Length(max = 500, message = "{profileAccount.avatar.length}")
    private String avatar;

    @NotNull(message = "{profileAccount.isActive.notNull}")
    private boolean isActive;

    private RoleDto role;
}
