package capstone.library.util.tools;

import capstone.library.repositories.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static capstone.library.util.constants.ConstantUtil.LIBRARY_ID;

@Service
public class BookCopyBarcodeUtils
{
    @Autowired
    BookCopyRepository bookCopyRepository;

    /*14 digits barcode
     * 1->2 Book copy type
     * 3->6 Library Id
     * 7->14 book copy id*/
    public List<String> generateBookCopyBarcode(int copyTypeId, int copyId, int numberOfCopies)
    {
        List<String> barcodes = new ArrayList<>();
        String tmp = "";

        if (copyTypeId >= 0 && copyTypeId < 100)
        {
            tmp += String.format("%02d", copyTypeId);
            tmp += String.format("%04d", LIBRARY_ID);
        }

        for (int i = 0; i < numberOfCopies; i++)
        {
            String barcode = tmp + String.format("%08d", ++copyId);
            barcodes.add(barcode);
        }

        return barcodes;
    }
}
