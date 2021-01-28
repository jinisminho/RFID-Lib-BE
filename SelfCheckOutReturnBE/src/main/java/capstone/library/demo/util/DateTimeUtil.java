package capstone.library.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static String convertDateTimeToString (LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtil.DATE_TIME_PATTERN);
        return date.format(formatter);
    }
}
