package capstone.library.dtos.response;


import capstone.library.dtos.common.MyAccountDto;
import capstone.library.dtos.common.MyBookDto;
import capstone.library.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CopyResponseDto implements Serializable
{
    private int id;

    private String barcode;

    private String rfid;

    private Double price;

    private BookCopyStatus status;

    private MyBookDto book;

    private String copyType;

    private MyAccountDto borrower;
}
