/*Tram*/
package capstone.library.dtos.response;

import capstone.library.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PatronAccountResponse {

    private int id;

    private String email;

    @JsonIgnore
    private String password;

    private String rfid;

    private String avatar;

    private boolean isActive;

    private PatronAccountResponse.ProfileDto profile;

    private PatronAccountResponse.PatronTypeDto patronType;

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
    private static class PatronTypeDto
    {
        private Integer id;

        private String name;
    }

}
