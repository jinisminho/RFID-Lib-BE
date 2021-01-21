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
public class ProfileResDto {

    private Integer id;

    @NotNull(message = "{profile.fullname.notNull}")
    @Length(max = 50, message = "{profile.fullname.length}")
    private String fullName;

    @NotNull(message = "{profile.phone.notNull}")
    @Length(max = 10, message = "{profile.phone.length}")
    private String phone;

    @NotNull(message = "{profile.gender.notNull}")
    private Gender gender;

    private ProfileAccountResDto account;
}