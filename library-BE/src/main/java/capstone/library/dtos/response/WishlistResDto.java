package capstone.library.dtos.response;

import capstone.library.enums.WishListStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResDto {

    private Integer id;

    private LocalDateTime createdAt;

    private WishListStatus status;

    private String email;

    private ProfileAccountResDto patron;

    private BookResDto book;


}
