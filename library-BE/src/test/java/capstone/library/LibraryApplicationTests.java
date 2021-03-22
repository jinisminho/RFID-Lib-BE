package capstone.library;

import capstone.library.entities.BookCopy;
import capstone.library.exceptions.PrintBarcodeException;
import capstone.library.repositories.GenreRepository;
import capstone.library.util.tools.PasswordUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.jboss.jdeparser.JDocComment;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

import static capstone.library.util.constants.BarcodeLabelConstant.LABEL_LENGTH;
import static capstone.library.util.constants.BarcodeLabelConstant.LABEL_WIDTH;

@RunWith(SpringRunner.class)
class LibraryApplicationTests {

    @Mock
    GenreRepository genreRepository;

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));
    }

    @Test
    void test() {
        System.out.println(Integer.parseInt("100.0"));
    }


    @Test
    void testGenerateRawPassword() {
        System.out.println(PasswordUtil.generatePassword());
    }


    @Test
    void printBarcode(){

    }

}
