package capstone.library.dtos.response;

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
public class AuthorResDto {

    private int id;

    @NotNull(message = "{author.name.notNull}")
    @Length(max = 100, message = "{author.name.length}")
    private String name;

}
