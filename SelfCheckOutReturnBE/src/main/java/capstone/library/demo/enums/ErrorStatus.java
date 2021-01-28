package capstone.library.demo.enums;

import org.springframework.http.HttpStatus;

public enum ErrorStatus {

    SYSTEM_ERROR (420, "System Error"),
    INVALID_DATA_FIELD (421, "Invalid Data"),
    RESOURCE_NOT_FOUND (422, "Resource Not Found"),
    MISSING_INPUT (423, "Missing Input"),
    DATABASE_INTEGRITY_VIOLATION (424, "Violate Database Constraints"),

    //Business
    BORROWING_POLICY_VIOLATION (450, "Violate Book Borrow Policy");

    private final int value;
    private final String reasonPhrase;

    private ErrorStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}
