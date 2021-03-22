package capstone.library.demo.service;

import capstone.library.demo.dtos.request.CheckOutBookRequest;
import capstone.library.demo.dtos.response.BookCheckOutResponse;
import capstone.library.demo.repositories.*;
import capstone.library.demo.services.SecurityGateService;
import capstone.library.demo.services.impl.BookBorrowingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutService {

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private BookCopyRepository bookCopyRepo;

    @Mock
    private BookBorrowingRepository bookBorrowingRepo;

    @Mock
    private BorrowPolicyRepository borrowPolicyRepo;

    @Mock
    private PatronTypeRepository patronTypeRepo;

    @Mock
    private FeePolicyRepository feePolicyRepo;

    @Mock
    private BorrowingRepository borrowingRepo;

    @Mock
    private SecurityGateService securityGateService;

    @Mock
    private GenreRepository genreRepository;


    @InjectMocks
    private BookBorrowingServiceImpl checkoutService;

    private List<CheckOutBookRequest> createCheckOutRequest(){
        List<CheckOutBookRequest> rs = new ArrayList<>();

        CheckOutBookRequest book1 = new CheckOutBookRequest();
        book1.setRfid("E28068940000500BB95740AE");
        book1.setBookId(1);
        book1.setGroup("REFERENCE");
        book1.setTitle("Hobbit");
        book1.setGroupId(1);
        rs.add(book1);

        CheckOutBookRequest book2 = new CheckOutBookRequest();
        book2.setRfid("E28068940000500BB95741AE");
        book2.setBookId(2);
        book2.setGroup("REFERENCE");
        book2.setTitle("Acadian");
        book2.setGroupId(1);
        rs.add(book1);

        return  rs;
    }

    private List<BookCheckOutResponse> createBookCheckoutResponse(){
        List<BookCheckOutResponse> rs = new ArrayList<>();
        BookCheckOutResponse b1 = new BookCheckOutResponse();
        b1.setGenres("General");
        b1.setAuthors("Dan");
        b1.setAbleToBorrow(true);
        b1.setEdition(1);
        b1.setGroup("REFERENCE");
        b1.setAbleToBorrow(true);
        return rs;
    }



}
