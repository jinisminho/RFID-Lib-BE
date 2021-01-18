package capstone.library.dtos.response;

import capstone.library.entities.Account;
import capstone.library.entities.BookBorrowing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendHistoryResDto {

    private Integer id;

    @NotNull(message = "{profile.phone.notNull}")
    @Length(max = 10, message = "{profile.phone.length}")


    @NotNull(message = "{profile.gender.notNull}")
    private LocalDateTime borrowedAt;

    private LocalDateTime extendedAt;

    private int extendIndex;

    private LocalDate dueAt;

    private BookBorrowing bookBorrowing;

    private Account librarian;
}