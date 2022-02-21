package capstone.library.security.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static capstone.library.util.constants.SecurityConstant.TOKEN_TYPE;

@Getter
@Setter

public class LoginResponse {

    private String accessToken;

    private final String tokenType = TOKEN_TYPE;

    private Integer userId;

    private Date expiryDate;

    private String role;

    private String patronGroup;

    private String email;

    private String avatar;

    public LoginResponse(String accessToken, Integer userId, Date expiryDate, String role, String patronGroup, String email, String avatar) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.role = role;
        this.patronGroup = patronGroup;
        this.email = email;
        this.avatar = avatar;
    }
}
