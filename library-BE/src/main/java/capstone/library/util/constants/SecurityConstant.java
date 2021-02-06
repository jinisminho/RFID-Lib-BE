package capstone.library.util.constants;

public class SecurityConstant {

    public static final String LOGIN_URL = "/auth/login";
    public static final String SECRET = "JWTSuperSecretKey";
    public static final int EXPIRATION_TIME = 7 *24 * 60 * 60;
    public static final String TOKEN_TYPE = "Bearer";
    public static final String HEADER_STRING = "Authorization";
}
