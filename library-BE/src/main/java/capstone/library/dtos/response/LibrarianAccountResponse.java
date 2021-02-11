package capstone.library.dtos.response;

import capstone.library.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LibrarianAccountResponse {

    private int id;

    private String email;

    @JsonIgnore
    private String password;

    private String rfid;

    private String avatar;

    private boolean isActive;

    private LibrarianAccountResponse.ProfileDto profile;


    @Getter
    @Setter
    @NoArgsConstructor
    private static class ProfileDto
    {
        private String fullName;

        private String phone;

        private Gender gender;
    }
}
