package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MyAccountDto implements Serializable
{
    private Integer id;

    private String email;

    private String rfid;

    private String avatar;

    private boolean isActive;

    private String roleName;

    private String patronTypeName;

    private MyProfileDto profile;
}
