package capstone.library.dtos.response;

import capstone.library.dtos.common.CheckoutCopyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponseDto implements Serializable
{
    private List<CheckoutCopyDto> checkoutCopyDto;
    private String borrowedAt;

}
