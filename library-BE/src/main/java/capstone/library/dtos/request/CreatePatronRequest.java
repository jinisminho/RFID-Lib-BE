package capstone.library.dtos.request;

import capstone.library.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatronRequest {

    @Email(message = "{account.email.invalid}")
    @NotNull(message = "{account.email.notNull}")
    private String email;

    @NotNull(message = "{account.rfid.notNull}")
    @Length(max = 80)
    private String rfid;

    @Length(max = 500)
    private String avatar;

    @NotNull(message = "{account.creatorId.notNull}")
    private Integer creatorId;

    @NotNull(message = "{account.patronTypeId.notNull}")
    private Integer patronTypeId;

    @NotNull(message = "{account.fullName.notNull}")
    @Length(min = 1, max = 50)
    private String fullName;

    @NotNull(message = "{account.phone.notNull}")
    @Length(max = 10, min = 10)
    private String phone;

    @NotNull(message = "{account.gender.notNull}")
    private Gender gender;
}
