package capstone.library.dtos.response;

import capstone.library.dtos.common.MyAccountDto;
import capstone.library.dtos.common.MyBookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnBookResponseDto implements Serializable {
    private int id;

    private String barcode;

    private String rfid;

    private Double price;

    private MyBookDto book;

    private int overdueDays;

    private String reason;

    private String dueDate;

    private double fine;

    private double bookPrice;

    private boolean overdue;

    private String borrowedAt;

    private String returnedAt;

    private String copyType;

    private MyAccountDto borrower;
}
