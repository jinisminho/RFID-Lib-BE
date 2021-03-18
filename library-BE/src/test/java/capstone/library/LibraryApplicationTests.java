package capstone.library;

import capstone.library.repositories.GenreRepository;
import capstone.library.util.tools.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class LibraryApplicationTests {

    @Mock
    GenreRepository genreRepository;

    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("4123"));
    }

    @Test
    void test() {
        System.out.println(Integer.parseInt("100.0"));
    }


    @Test
    void testGenerateRawPassword() {
        System.out.println(PasswordUtil.generatePassword());
    }

}
