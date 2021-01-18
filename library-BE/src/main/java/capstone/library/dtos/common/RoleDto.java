package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Integer id;

    @Length(max = 20, message = "{role.name.length}")
    private String name;

}
