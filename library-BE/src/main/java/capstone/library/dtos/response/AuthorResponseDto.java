package capstone.library.dtos.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Author Info")
public class AuthorResponseDto
{
    private int id;

    private String name;

    //Tram added country and birthYear
    private String country;

    private Integer birthYear;

}
