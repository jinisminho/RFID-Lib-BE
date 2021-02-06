package capstone.library.dtos.request;

import capstone.library.util.constants.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateReqDto implements Serializable
{

    @NotNull(message = "{phone.notNull}")
    @Length(max = 10, message = "{phone.maxLength}")
    @Pattern(regexp = ConstantUtil.PHONE_REGEXP, message = "{phone.pattern}")
    private String phone;

}
