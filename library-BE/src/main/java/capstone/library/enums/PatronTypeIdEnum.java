package capstone.library.enums;

public enum PatronTypeIdEnum
{
    PATRON_TYPE_STUDENT(1),
    PATRON_TYPE_LECTURER(2);

    private int patronTypeId;

    PatronTypeIdEnum(int patronTypeId)
    {
        this.patronTypeId = patronTypeId;
    }

    public int getPatronTypeId()
    {
        return patronTypeId;
    }
}
