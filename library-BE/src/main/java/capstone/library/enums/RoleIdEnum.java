package capstone.library.enums;

public enum RoleIdEnum
{
    ROLE_ADMIN(1),
    ROLE_LIBRARIAN(2),
    ROLE_PATRON(3);

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
