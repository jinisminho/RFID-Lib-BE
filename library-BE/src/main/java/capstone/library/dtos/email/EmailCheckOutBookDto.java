package capstone.library.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class EmailCheckOutBookDto {

    @NotNull(message = "{EmailCheckOutBookDto.title.notNull}")
    private String title;

    private String subtitle;

    @NotNull(message = "{EmailCheckOutBookDto.dueDate.notNull}")
    private String dueDate;

    @NotNull(message = "{EmailCheckOutBookDto.borrowedAt.notNull}")
    private String borrowedAt;

    private int edition;

}
