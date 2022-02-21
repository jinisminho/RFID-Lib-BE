package capstone.library.dtos.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Account basic info")
public class AccountBasicInfoResponseDto implements Serializable
{
    private int id;

    private String email;

    private String avatar;

    private boolean isActive;

    private RoleDto role;

    private ProfileDto profile;

    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProfileDto
    {
        private String fullName;

        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class RoleDto
    {
        private int id;
        private String name;
    }
}

