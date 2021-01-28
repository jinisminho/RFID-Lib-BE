package capstone.library.enums;

public enum BookCopyTypeIdEnum
{
    REFERENCE_BOOK(1),
    TEXTBOOK(2),
    THESIS(3);

    private int bookCopyTypeId;

    BookCopyTypeIdEnum(int bookCopyTypeId)
    {
        this.bookCopyTypeId = bookCopyTypeId;
    }

    public int getBookCopyTypeId()
    {
        return bookCopyTypeId;
    }
}
