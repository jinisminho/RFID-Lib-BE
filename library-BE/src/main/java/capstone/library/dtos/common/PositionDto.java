package capstone.library.dtos.common;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PositionDto {

    private int line;
    private String shelf;
    private int id;
}
