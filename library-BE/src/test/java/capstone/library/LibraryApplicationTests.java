package capstone.library;

import capstone.library.services.BookCopyPositionService;
import capstone.library.services.impl.BookCopyPositionServiceImpl;
import capstone.library.util.tools.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

class LibraryApplicationTests
{



    @Test
    void contextLoads()
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));
    }


    @Test
    void testGenerateRawPassword()
    {
        System.out.println(PasswordUtil.generatePassword());
    }

}
