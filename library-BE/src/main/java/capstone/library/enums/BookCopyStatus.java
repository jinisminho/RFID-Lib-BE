package capstone.library.enums;


//reference: https://libraryguides.missouri.edu/c.php?g=583142&p=4026632
public enum BookCopyStatus
{
    IN_PROCESS,//PREPARING(old)
    AVAILABLE,
    BORROWED,
    OUT_OF_CIRCULATION,
    LOST,
    LIB_USE_ONLY,//NOT_ALLOW_TO_BORROW (old)
    DISCARD
}
