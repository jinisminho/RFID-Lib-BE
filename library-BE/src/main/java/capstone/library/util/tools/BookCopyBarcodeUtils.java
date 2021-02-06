package capstone.library.util.tools;

import capstone.library.entities.BookCopy;
import capstone.library.repositories.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public List<String> generateBookCopyBarcode(int copyTypeId)
    {
        List<String> barcodes = new ArrayList<>();
        String barcode = "";
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findFirstByOrderByIdDesc();

        if (copyTypeId >= 0 && copyTypeId < 100)
        {
            barcode += String.format("%02d", copyTypeId);
            barcode += String.format("%04d", LIBRARY_ID);
            if (bookCopyOptional.isPresent())
            {
                System.out.println(bookCopyOptional.get().getBook().getTitle());
                int id = bookCopyOptional.get().getId();
                barcode += String.format("%08d", id);
            }
        }

        return barcodes;
    }
}
