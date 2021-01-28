package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCopiesRequestDto
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
}
