package capstone.library.dtos.response;

import capstone.library.enums.Gender;
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

    private Integer profileId;
    
    private Integer accountId;

    @NotNull(message = "{profile.fullname.notNull}")
    @Length(max = 50, message = "{profile.fullname.length}")
    private String fullName;

    @NotNull(message = "{profile.phone.notNull}")
    @Length(max = 10, message = "{profile.phone.length}")
    private String phone;

    @NotNull(message = "{profile.gender.notNull}")
    private Gender gender;


    @NotNull(message = "{profileAccount.email.notNull}")
    @Length(max = 100, message = "{profileAccount.email.length}")
    private String email;

    @Length(max = 80, message = "{profileAccount.rfid.length}")
    private String rfid;

    @Length(max = 500, message = "{profileAccount.avatar.length}")
    private String avatar;

    @NotNull(message = "{profileAccount.isActive.notNull}")
    private boolean isActive;

    private String role;

}
