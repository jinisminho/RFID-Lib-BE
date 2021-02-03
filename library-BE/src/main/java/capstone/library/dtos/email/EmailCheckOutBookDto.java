package capstone.library.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class EmailCheckOutBookDto {

    @NotNull
    private String title;

    private String subtitle;

    @NotNull
    private String dueDate;

    @NotNull
    private String borrowedAt;

    private int edition;

}
