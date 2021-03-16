package capstone.library.services.impl;

import capstone.library.dtos.request.AddPortableSearchingBooksRequest;
import capstone.library.dtos.response.PortableSearchBookResponse;
import capstone.library.entities.Account;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookEntityRepository;
import capstone.library.repositories.BookRepository;
import capstone.library.services.PortableSearchBookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PortableSearchBookServiceImpl implements PortableSearchBookService {

    public static final String FILE_DIRECTORY = "src/main/java/capstone/library/files/";

    @Autowired
    private BookEntityRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<PortableSearchBookResponse> findBookSearchingList(int patronId) {
        return null;
    }

    @Override
    public String addSearchingBooksToFile(AddPortableSearchingBooksRequest request) {
        if (request == null) {
            throw new MissingInputException("missing request");
        }
        accountRepository.findById(request.getPatronId())
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find patron with id: " + request.getPatronId()));

        List<Integer> tmp = request.getBookIdList().stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        request.setBookIdList(tmp);

        try {
            String requestJson = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert object to json");
        }
        return null;
    }
}
