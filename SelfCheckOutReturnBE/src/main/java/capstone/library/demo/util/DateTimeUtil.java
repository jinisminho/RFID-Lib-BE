package capstone.library.demo.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DateTimeUtil {

    public static String convertDateTimeToString (LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtil.DATE_TIME_PATTERN);
        return date.format(formatter);
    }

    public static String convertDateToString (LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantUtil.DATE_PATTERN);
        return date.format(formatter);
    }

    public static long getOverdueDays(LocalDate today, LocalDate dueDate)
    {
        /*Get overdue days disregard of holidays.
         * In case of wanting to include holidays, replace 3rd param with List<LocalDate> holidays*/
        return countBusinessDaysBetween(today, dueDate, Optional.empty());
    }

    /*https://howtodoinjava.com/java/date-time/calculate-business-days/*/
    private static long countBusinessDaysBetween(LocalDate today, LocalDate dueDate, Optional<List<LocalDate>> holidaysOptional)
    {
        List<LocalDate> holidays = new ArrayList<>();

        if (today == null || dueDate == null || holidaysOptional == null)
        {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + today
                    + "," + dueDate + "," + holidaysOptional + ")");
        } else if (holidaysOptional.isPresent())
        {
            holidays = holidaysOptional.get();

            for (LocalDate holiday : holidays)
            {
                if (holiday.equals(dueDate))
                {
                    dueDate = dueDate.plusDays(1);
                }
            }
        }

        while (dueDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dueDate.getDayOfWeek().equals(DayOfWeek.SUNDAY))
        {
            dueDate = dueDate.plusDays(1);
        }

        Predicate<LocalDate> isHoliday = date -> holidaysOptional.map(localDates -> localDates.contains(date)).orElse(false);

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(today, dueDate);

        if (daysBetween < 0)
        {
            daysBetween = -daysBetween;
            return (Stream.iterate(today, date -> date.minusDays(1)).limit(daysBetween)
                    .filter(isHoliday.or(isWeekend).negate()).count());
        } else
        {
            return -daysBetween;
        }

    }

}
