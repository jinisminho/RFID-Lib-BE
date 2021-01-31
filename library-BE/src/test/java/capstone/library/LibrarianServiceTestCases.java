package capstone.library;

import capstone.library.controllers.web.LibrarianController;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(LibrarianController.class)
public class LibrarianServiceTestCases
{

    /*    @TestConfiguration
        static class LibrarianServiceImplTestContextConfiguration
        {
            @Bean
            public LibrarianService librarianService()
            {
                return new LibrarianServiceImpl();
            }
        }
    
        @Autowired
        private LibrarianService librarianService;
    
        @MockBean
        BookCopyRepository bookCopyRepository;
        @MockBean
        BorrowPolicyRepository borrowPolicyRepository;
        @MockBean
        BookBorrowingRepository bookBorrowingRepository;
        @MockBean
        AccountRepository accountRepository;
    
        @Before
        public void setUp()
        {
            int studentId = 7;
            int librarianId = 2;
            int managerId = 1;
    
            Mockito.when(accountRepository.findByIdAndRoleId(7, 1)).thenReturn();
        }*/
}
