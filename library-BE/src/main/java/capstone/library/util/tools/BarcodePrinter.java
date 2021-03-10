package capstone.library.util.tools;

import capstone.library.exceptions.PrintBarcodeException;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

public class BarcodePrinter {


    public static void createImage(String myString)  {
        try {
            Code39Bean code39Bean = new Code39Bean();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", 160, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code39Bean.generateBarcode(canvas, myString);
            canvas.finish();
            //write to png file
            FileOutputStream fos = new FileOutputStream("barcode.png");
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            throw new PrintBarcodeException(e.getMessage());
        }
    }


}
