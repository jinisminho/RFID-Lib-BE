package capstone.library.dtos.common;

import capstone.library.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyProfileDto implements Serializable
{
    private Integer id;

    private String fullName;

    private String phone;

    private Gender gender;

}
