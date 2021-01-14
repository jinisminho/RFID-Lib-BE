package capstone.library.dtos;

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
public class CategoryWithBooksDto {
    private int id;

    @NotNull(message = "{category.name.notNull}")
    @Length(max = 100, message = "{category.name.length}")
    private String name;

    public Set<BookDto> books;

}
