package capstone.library.demo.enums;

import org.springframework.http.HttpStatus;

public enum ErrorStatus {

    BORROWING_POLICY_VIOLATION (421, "Violate Book Borrow Policy");

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
