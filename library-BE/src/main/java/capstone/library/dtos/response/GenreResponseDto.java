package capstone.library.dtos.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Genre Info")
public class GenreResponseDto
{
    private int id;

    private String name;
}
