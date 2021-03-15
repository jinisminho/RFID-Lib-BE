package capstone.library.util.tools;

public class PriceFormatter {

     public static String formatPrice (double price){
         if(price == (long) price)
             return String.format("%d",(long)price);
         else
             return String.format("%s",price);
     }
}
