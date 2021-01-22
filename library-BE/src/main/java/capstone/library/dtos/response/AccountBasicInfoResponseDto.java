package capstone.library.dtos.response;

import capstone.library.enums.Gender;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Account basic info")
public class AccountBasicInfoResponseDto
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

