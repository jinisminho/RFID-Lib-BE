package capstone.library.dtos;

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
public class GenreDto {
    private int id;

    @NotNull(message = "{genre.name.notNull}")
    @Length(max = 100, message = "{genre.name.length}")
    private String name;

}
