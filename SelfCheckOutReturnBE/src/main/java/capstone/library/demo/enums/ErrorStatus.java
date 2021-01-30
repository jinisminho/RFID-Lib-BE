package capstone.library.demo.enums;

public enum ErrorStatus {

    SYSTEM_ERROR(420, "System Error"),
    INVALID_DATA_FIELD(421, "Invalid Data"),
    RESOURCE_NOT_FOUND(422, "Resource Not Found"),
    MISSING_INPUT(423, "Missing Input"),
    DATABASE_INTEGRITY_VIOLATION(424, "Violate Database Constraints"),

    //Business
    BORROWING_POLICY_VIOLATION(450, "Violate Book Borrow Policy");

    private final int code;
    private final String reason;

    private ErrorStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return this.code;
    }

    public String getReason() {
        return this.reason;
    }
}
