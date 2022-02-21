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
public class CreateAuthorReqDto {
    @NotNull(message = "{author.name.notNull}")
    @Length(max = 50, message = "{author.name.length}")
    private String name;

    @NotNull(message = "{author.country.notNull}")
    @Length(max = 50, message = "{author.country.length}")
    private String country;

    @NotNull(message = "{author.birthDay.notNull}")
    private Integer birthYear;
}
