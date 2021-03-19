package capstone.library.serive;

import capstone.library.entities.Account;
import capstone.library.entities.Role;
import capstone.library.repositories.*;
import capstone.library.services.LibrarianService;
import capstone.library.services.impl.LibrarianServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibrarianCheckoutTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private FeePolicyRepository feePolicyRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;
    @Mock
    private BorrowPolicyRepository borrowPolicyRepository;
    @Mock
    private BookBorrowingRepository bookBorrowingRepository;

    @InjectMocks
    private LibrarianService librarianService;

    private static final int ADMIN_ACCOUNT_ID = 1;
    private static final int PATRON_ACCOUNT_ID = 7;


    Role librarianRole = new Role();
    Account librarian = new Account();

    @Before
    public void init() {
        librarianService = new LibrarianServiceImpl();
    }

    @Test
    void testCheckoutSuccessful() {

        when(accountRepository.findById(ADMIN_ACCOUNT_ID)).thenReturn(Optional.of(librarian));
//        assertEquals()
    }
}
