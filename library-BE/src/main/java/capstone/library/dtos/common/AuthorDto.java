package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private int id;

    @NotNull(message = "{author.name.notNull}")
    @Length(max = 100, message = "{author.name.length}")
    private String name;

    private Set<BookDto> books;

}
