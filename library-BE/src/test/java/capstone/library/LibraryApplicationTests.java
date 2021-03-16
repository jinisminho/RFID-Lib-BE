package capstone.library;

import capstone.library.util.tools.CallNumberUtil;
import capstone.library.util.tools.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class LibraryApplicationTests {


    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("4123"));
    }

    @Test
    void test() {
        CallNumberUtil callNumberUtil = new CallNumberUtil();
        System.out.println(callNumberUtil.createCallNumber(250, "J.K Rowling, Harry Potter", 2010));
    }


    @Test
    void testGenerateRawPassword() {
        System.out.println(PasswordUtil.generatePassword());
    }

}
