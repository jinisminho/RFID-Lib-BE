package capstone.library.enums;

public enum DbStatusEnum
{
    SUCCESS("1"),
    PENDING("-1"),
    FAILED("0");

    private String statusCode;

    DbStatusEnum(String statusCode)
    {
        this.statusCode = statusCode;
    }

    public String getStatusCode()
    {
        return statusCode;
    }
}
