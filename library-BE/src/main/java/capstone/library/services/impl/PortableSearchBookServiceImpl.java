package capstone.library.services.impl;

import capstone.library.dtos.request.AddPortableSearchingBooksRequest;
import capstone.library.dtos.response.PortableBookSearchPositionResponse;
import capstone.library.dtos.response.PortableSearchBookResponse;
import capstone.library.entities.Book;
import capstone.library.entities.BookCopy;
import capstone.library.enums.BookCopyStatus;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookEntityRepository;
import capstone.library.services.PortableSearchBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static capstone.library.util.constants.ConstantUtil.CREATE_SUCCESS;
import static capstone.library.util.constants.ConstantUtil.DELETE_SUCCESS;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
public class PortableSearchBookServiceImpl implements PortableSearchBookService {

    private static final String FILE_DIRECTORY = "src/main/java/capstone/library/files/";

    private static final String BOOK_FILE_PREFIX = "BOOK";

    private static final String FILE_TEXT_EXTENSION = ".txt";

    @Autowired
    private BookEntityRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<PortableSearchBookResponse> findBookSearchingList(int accountId) {
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find patron with id: " + accountId));

        String fileName = BOOK_FILE_PREFIX + "-" + accountId + FILE_TEXT_EXTENSION;
        String filePath = FILE_DIRECTORY + fileName;

        List<PortableSearchBookResponse> result = new ArrayList<>();
        AddPortableSearchingBooksRequest itemInFile = readFile(filePath);

        //search tung cuon sach xem co available + lib-use ok
        //neu available thi tra ve position cua copy available + lib-use vaf rfid cua no
        Set<BookCopyStatus> availableCopyStatus = new HashSet<>();
        availableCopyStatus.add(BookCopyStatus.AVAILABLE);
        availableCopyStatus.add(BookCopyStatus.LIB_USE_ONLY);
        for (Integer bookId : itemInFile.getBookIdList()) {
            Book book = bookRepository.findById(bookId)
                    .orElse(null);
            if (book != null) {
                List<BookCopy> copies = bookCopyRepository.findByBookIdAndStatusIn(bookId, availableCopyStatus);
                PortableSearchBookResponse tmp = mapper.convertValue(book, PortableSearchBookResponse.class);
                String authors = book.getBookAuthors()
                        .stream()
                        .map(a -> a.getAuthor().getName())
                        .collect(Collectors.joining(", "));
                tmp.setAuthorNames(authors);
                if (copies.isEmpty()) {
                    tmp.setAvailable(false);
                    tmp.setPositionList(new ArrayList<>());
                    tmp.setCopyRfidList(new ArrayList<>());
                } else {
                    tmp.setAvailable(true);
                    List<String> copyRfids = copies.stream().map(BookCopy::getRfid).collect(Collectors.toList());
                    tmp.setCopyRfidList(copyRfids);
                    //extract all positions from available copies
                    List<PortableBookSearchPositionResponse>
                            positions = copies.stream().map(
                            c -> {
                                return new PortableBookSearchPositionResponse(c.getBookCopyPosition().getShelf(),
                                        c.getBookCopyPosition().getLine().toString());
                            }
                    ).collect(Collectors.toList());
                    //only get unique positions
                    List<PortableBookSearchPositionResponse> uniquePositions = positions
                            .stream()
                            .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(PortableBookSearchPositionResponse::toString))),
                                    ArrayList::new));
                    tmp.setPositionList(uniquePositions);
                }
                result.add(tmp);

            }
        }
        return result;
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

        String fileName = BOOK_FILE_PREFIX + "-" + request.getPatronId() + FILE_TEXT_EXTENSION;
        String filePath = FILE_DIRECTORY + fileName;

        //if the file exist combine adding book list with existing book list in that file
        File file = new File(filePath);
        if (file.exists()) {
            AddPortableSearchingBooksRequest existingList = readFile(filePath);
            List<Integer> combineList = Stream
                    .concat(existingList.getBookIdList().stream(), request.getBookIdList().stream())
                    .distinct()
                    .collect(Collectors.toList());
            request.setBookIdList(combineList);
        }

        writeFile(filePath, request);
        return CREATE_SUCCESS;
    }

    @Override
    public String deleteSearchingFile(int accountId) {
        boolean isDeleted;
        accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account",
                        "Cannot find account with id: " + accountId));
        String fileName = BOOK_FILE_PREFIX + "-" + accountId + FILE_TEXT_EXTENSION;
        String filePath = FILE_DIRECTORY + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            isDeleted = file.delete();
        } else {
            throw new RuntimeException("Cannot find the file");
        }
        if (isDeleted) {
            return DELETE_SUCCESS;
        } else {
            throw new RuntimeException("Cannot delete the file");
        }

    }

    private void writeFile(String filePath, AddPortableSearchingBooksRequest request) {
        try {
            mapper.writeValue(new File(filePath), request);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private AddPortableSearchingBooksRequest readFile(String filePath) {
        try {
            return mapper.
                    readValue(new File(filePath), AddPortableSearchingBooksRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
