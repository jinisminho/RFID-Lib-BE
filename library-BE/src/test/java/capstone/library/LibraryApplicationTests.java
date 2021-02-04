package capstone.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;


class LibraryApplicationTests
{

    @Test
    void contextLoads()
    {
        LocalDate curDate = LocalDate.of(2021, 1,31);

        System.out.println("now: " + curDate);
        System.out.println("tmr: " + curDate.plusDays(1));
    }

}
