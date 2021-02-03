package capstone.library.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;


class SelfCheckOutReturnBeApplicationTests {

	@Test
	void contextLoads() {
		LocalDate localDate1 = LocalDate.now();
		System.out.println(localDate1);
		LocalDate localDate2 = LocalDate.of(2021, 02, 02);
		System.out.println(localDate2);
		System.out.println(localDate1.compareTo(localDate2));
		System.out.println(Duration.between(localDate1.atStartOfDay(), localDate2.atStartOfDay()).toDays());
		 // another option

	}

}
