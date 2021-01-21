package capstone.library.enums;

public enum AccountStatusEnum
{
    STATUS_ACTIVE(true),
    STATUS_INACTIVE(false);

    private boolean status;

    AccountStatusEnum(boolean status)
    {
        this.status = status;
    }

    public boolean getStatus()
    {
        return status;
    }
}
