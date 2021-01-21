package capstone.library.enums;

public enum RoleIdEnum
{
    ROLE_ADMIN(1),
    ROLE_LIBRARIAN(2),
    ROLE_PATRON(3);

    private int statusCode;

    RoleIdEnum(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int getRoleId()
    {
        return statusCode;
    }
}
