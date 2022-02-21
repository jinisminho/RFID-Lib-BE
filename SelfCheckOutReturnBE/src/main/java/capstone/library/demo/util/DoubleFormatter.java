package capstone.library.demo.util;

public class DoubleFormatter {

    public static String formatToDecimal(double price){
        if(price == (long) price)
            return String.format("%d",(long)price);
        else
            return String.format("%s",price);
    }
}
