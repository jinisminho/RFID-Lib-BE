package capstone.library.enums;

public enum RoleIdEnum
{
    ADMIN(1),
    LIBRARIAN(2),
    PATRON(3);

    private int roleId;

    RoleIdEnum(int roleId)
    {
        this.roleId = roleId;
    }

    public int getRoleId()
    {
        return roleId;
    }
}
