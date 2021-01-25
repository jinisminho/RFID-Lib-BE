package capstone.library.util;

public class ConstantUtil {
    private ConstantUtil() {

    }

    public static final String UPDATE_SUCCESS = "Updated successful";

    public static final String DELETE_SUCCESS = "Deleted successful";

    public static final String CREATE_SUCCESS = "Created successful";

    public static final String EXCEPTION_FK_DUPLICATED = "Resource conflicted";

    public static final String EXCEPTION_UNEXPECTED_ERROR = "Server error";

    public static final String EXCEPTION_INVALID_REQUEST = "Request is invalid";

    public static final String EXCEPTION_UNAUTHORIZED = "This user is not authorized";

    public static final String EXCEPTION_RESOURCE_NOT_FOUND = "Resource not found";

    public static final String EXCEPTION_VALIDATION_FAILED = "Update to database failed";

    public static final String EXCEPTION_POLICY_VIOLATION = "Violated the policy";

    //Other
    public static final String PHONE_REGEXP = "^\\d{10}$";
}
