package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendHistoryResDto {

    private Integer id;

    @NotNull(message = "{extendHistory.borrowedAt.notNull}")
    private LocalDateTime borrowedAt;

    private LocalDateTime extendedAt;

    private int extendIndex;

    @NotNull(message = "{extendHistory.borrowedAt.notNull}")
    private LocalDate dueAt;

    private ProfileAccountResDto issuedBy;

}