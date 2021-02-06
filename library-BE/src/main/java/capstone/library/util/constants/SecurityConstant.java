package capstone.library.util.constants;

import org.springframework.orm.hibernate5.SpringFlushSynchronization;

public class SecurityConstant {

    public static final String LOGIN_URL = "/auth/login";
    public static final String SECRET = "JWTSuperSecretKey";
    public static final int EXPIRATION_TIME = 7 *24 * 60 * 60;
    public static final String TOKEN_TYPE = "Bearer";
    public static final String HEADER_STRING = "Authorization";

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String LIBRARIAN = "ROLE_LIBRARIAN";
    public static final String PATRON = "ROLE_PATRON";

}
