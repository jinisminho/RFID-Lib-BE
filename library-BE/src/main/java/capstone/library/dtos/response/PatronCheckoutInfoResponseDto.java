package capstone.library.dtos.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("Checkout patron detail")
public class PatronCheckoutInfoResponseDto
{
    AccountDetailResponseDto patronAccountInfo;
    List<ReturnBookResponseDto> overdueBooks;
}
