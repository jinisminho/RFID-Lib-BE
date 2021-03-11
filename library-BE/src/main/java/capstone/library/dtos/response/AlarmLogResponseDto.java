package capstone.library.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AlarmLogResponseDto {
    private int id;
    private CopyResponseDto bookCopy;
    private LocalDateTime loggedAt;
}
