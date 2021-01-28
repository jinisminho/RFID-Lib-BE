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
public class CreateLibrarianRequestDto
{
    @NotNull(message = "{CreateLibrarianRequestDto.email.notNull}")
    @Email(message = "{CreateLibrarianRequestDto.email.emailFormat}")
    private String email;

    @NotNull(message = "{CreateLibrarianRequestDto.password.notNull}")
    private String password;

    @Length(max = 500, message = "{CreateLibrarianRequestDto.avatar.maxLength}")
    private String avatar;

    @NotNull(message = "{CreateLibrarianRequestDto.creator.notNull}")
    private int creator;

    @NotNull(message = "{CreateLibrarianRequestDto.updater.notNull}")
    private int updater;

    @NotNull(message = "{CreateLibrarianRequestDto.fullName.notNull}")
    @Length(max = 80, message = "{CreateLibrarianRequestDto.fullName.maxLength}")
    private String fullName;

    @NotNull(message = "{CreateLibrarianRequestDto.phone.notNull}")
    @Length(min = 10, max = 10, message = "{CreateLibrarianRequestDto.phone.length}")
    private String phone;

    @NotNull(message = "{CreateLibrarianRequestDto.gender.notNull}")
    private Gender gender;
}
