package capstone.library.dtos.response;

import capstone.library.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResDto {

    private Integer id;

    @NotNull(message = "{bookCopy.barcode.notNull}")
    @Length(max = 100, message = "{bookCopy.barcode.length}")
    private String barcode;

    @Length(max = 80, message = "{bookCopy.rfid.length}")
    private String rfid;

    @NotNull(message = "{bookCopy.price.notNull}")
    private Double price;

    @NotNull(message = "{bookCopy.status.notNull}")
    @Length(max = 20, message = "{bookCopy.status.length}")
    private BookCopyStatus status;

    private BookResDto book;
}
