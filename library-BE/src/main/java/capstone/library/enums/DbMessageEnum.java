package capstone.library.enums;

public enum DbMessageEnum
{
    SUCCESS("Update to database successfully"),
    PENDING("Database is not used"),
    FAILED("Update to database failed");

    private String message;

    DbMessageEnum(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
