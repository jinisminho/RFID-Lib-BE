package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCopiesRequestDto implements Serializable
{
    @NotNull(message = "{CreateCopiesRequestDto.bookId.notNull}")
    int bookId;
    @NotNull(message = "{CreateCopiesRequestDto.copyTypeId.notNull}")
    int copyTypeId;
    @NotNull(message = "{CreateCopiesRequestDto.creatorId.notNull}")
    int creatorId;
    @NotNull(message = "{CreateCopiesRequestDto.barcodes.notNull}")
    Set<String> barcodes;
    @NotNull(message = "{CreateCopiesRequestDto.price.notNull}")
    double price;
    @NotNull(message = "{CreateCopiesRequestDto.priceNote.notNull}")
    @Length(max = 500, min = 1, message = "{CreateCopiesRequestDto.priceNote.length}")
    String priceNote;
}
