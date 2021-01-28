package capstone.library.dtos.response;


import capstone.library.dtos.common.MyBookDto;
import capstone.library.entities.Role;
import capstone.library.enums.BookCopyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CopyResponseDto
{
    private int id;

    private String barcode;

    private String rfid;

    private Double price;

    private BookCopyStatus status;

    private MyBookDto book;

    private String copyType;

    @JsonIgnore
    private AccountDto creator;

    @JsonIgnore
    private AccountDto updater;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AccountDto
    {
        private int id;

        private String email;

        private Role role;
    }
}
