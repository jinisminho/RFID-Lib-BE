package capstone.library.dtos.response;

import capstone.library.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Account detail")
public class AccountDetailResponseDto implements Serializable
{
    private int id;

    private String email;

    @JsonIgnore
    private String password;

    private String rfid;

    private String avatar;

    private boolean isActive;

    private RoleDto role;

    private ProfileDto profile;

    private PatronTypeDto patronType;

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProfileDto
    {
        private String fullName;

        private String phone;

        private Gender gender;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class RoleDto
    {
        private int id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class PatronTypeDto
    {
        private Integer id;

        private String name;
    }
}
