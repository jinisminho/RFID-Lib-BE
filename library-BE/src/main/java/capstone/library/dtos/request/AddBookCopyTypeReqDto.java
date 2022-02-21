package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class AddBookCopyTypeReqDto {
    @NotNull(message = "{bookCopyType.name.notNull}")
    private String name;
}
