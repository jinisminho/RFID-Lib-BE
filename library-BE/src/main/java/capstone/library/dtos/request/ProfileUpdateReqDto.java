package capstone.library.dtos.request;

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
public class ProfileUpdateReqDto {

    @NotNull(message = "{profile.phone.notNull}")
    @Length(max = 10, message = "{profile.phone.length}")
    private String phone;

}
