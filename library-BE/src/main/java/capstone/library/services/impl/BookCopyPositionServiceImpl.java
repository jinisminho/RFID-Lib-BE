package capstone.library.services.impl;

import capstone.library.dtos.common.PositionDto;
import capstone.library.dtos.request.SaveSamplePositionRequestDto;
import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyPosition;
import capstone.library.enums.BookCopyStatus;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyPositionRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyPositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookCopyPositionServiceImpl implements BookCopyPositionService {

    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    BookCopyPositionRepository positionRepository;
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ObjectMapper objectMapper;

    private static final String SUCCESS_MESSAGE = "Success";
    private static final String BOOK_COPY = "Book copy";
    private static final String POSITION = "Book copy position";
    private static final String UPDATER = "Account";
    private static final String BOOK_COPY_NOT_FOUND_ERROR = "Book copy not found: ";
    private static final String POSITION_NOT_FOUND_ERROR = "Book copy position not found";
    private static final String UPDATER_NOT_FOUND_ERROR = "Updater account not found: ";


    @Override
    public BookCopyPositionResponse findPositionForBookCopy(int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Copy",
                        "Cannot find copy with id:" + bookCopyId));
        String searchCallNumber = bookCopy.getBook().getCallNumber();
        BookCopyPosition position = bookCopy.getBookCopyPosition();
        if (position == null) {
            return new BookCopyPositionResponse(bookCopy.getBookCopyType().getName(),
                    bookCopy.getBook().getCallNumber(),
                    "N/A",
                    "N/A",
                    bookCopy.getStatus().toString());
        }
        return new BookCopyPositionResponse(bookCopy.getBookCopyType().getName(),
                bookCopy.getBook().getCallNumber(),
                position.getLine().toString(),
                position.getShelf(),
                bookCopy.getStatus().toString());
    }

    @Override
    public List<BookCopyPositionResponse> findPositionForBook(int bookId) {
        List<BookCopyPositionResponse> rs = new ArrayList<>();
        List<BookCopy> bookCopyList = bookCopyRepository.findByBookId(bookId);
        if (!bookCopyList.isEmpty()) {
            for (BookCopy copy : bookCopyList) {
                BookCopyPosition position = copy.getBookCopyPosition();
                BookCopyPositionResponse response = new BookCopyPositionResponse();
                response.setBookCopyType(copy.getBookCopyType().getName());
                response.setCallNumber(copy.getBook().getCallNumber());
                response.setStatus(copy.getStatus().toString());
                if (position != null) {
                    response.setLine(position.getLine().toString());
                    response.setShelf(position.getShelf());
                } else {
                    response.setLine("N/A");
                    response.setShelf("N/A");
                }
                rs.add(response);
            }

        }
        return rs;
    }

    @Override
    public Set<String> getAllShelves() {
        List<BookCopyPosition> positions = positionRepository.findAllGroupsByShelf();
        Set<String> shelves = new HashSet<>();
        for (BookCopyPosition shelf : positions) {
            shelves.add(shelf.getShelf());
        }
        return shelves;
    }

    @Override
    public List<BookCopyPosition> getRowByShelf(String shelf) {
        return positionRepository.findByShelf(shelf);
    }

    @Override
    @Transactional
    public String saveSampledPosition(SaveSamplePositionRequestDto request) {
        BookCopyPosition bookCopyPosition = positionRepository.findById(request.getPositionId()).
                orElseThrow(() -> new ResourceNotFoundException(POSITION, POSITION_NOT_FOUND_ERROR));

        for (String rfid : request.getRfids()) {
            BookCopy bookCopy = bookCopyRepository.findByRfid(rfid).
                    orElseThrow(() -> new ResourceNotFoundException(BOOK_COPY, BOOK_COPY_NOT_FOUND_ERROR + rfid));
            bookCopy.setBookCopyPosition(bookCopyPosition);
            bookCopy.setUpdater(accountRepository.findById(request.getUpdater()).
                    orElseThrow(() -> new ResourceNotFoundException(UPDATER, UPDATER_NOT_FOUND_ERROR)));
            bookCopyRepository.save(bookCopy);
        }
        return SUCCESS_MESSAGE;
    }

    @Override
    public List<CopyResponseDto> getBooksOnARow(int positionId) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookCopyPositionId(positionId);
        List<CopyResponseDto> response = new ArrayList<>();
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getStatus().equals(BookCopyStatus.LIB_USE_ONLY) ||
                    bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
                CopyResponseDto dto = objectMapper.convertValue(bookCopy, CopyResponseDto.class);
                dto.setPosition(objectMapper.convertValue(bookCopy.getBookCopyPosition(), PositionDto.class));
                response.add(dto);
            }
        }
        return response;
    }

//    private BookCopyPositionResponse findPositionOfBookCopy(String callNumber, BookCopyType bookCopyType){
//        List<BookCopyPosition> positions = positionRepository
//                .findByBookCopyTypeIdOrderByFromCallNumberAsc(bookCopyType.getId());
//        try {
//            int callNumberPrefix = Integer.parseInt(callNumber.substring(0, 3));
//            if (!positions.isEmpty()) {
//                BookCopyPosition holdingPosition = positions.get(0);
//                for (BookCopyPosition position : positions) {
//                    int tmpCallNumber = Integer.parseInt(position.getFromCallNumber());
//                    if (tmpCallNumber > callNumberPrefix) {
//                        break;
//                    }else{
//                        holdingPosition = position;
//                    }
//                }
//                return new BookCopyPositionResponse(bookCopyType.getName()
//                        , callNumber, holdingPosition.getFloor() + "", holdingPosition.getShelf());
//
//            } else {
//                return new BookCopyPositionResponse(bookCopyType.getName(), callNumber, "Not found", "Not found");
//            }
//        }catch (NumberFormatException e){
//            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Number Format Exception", "Cannot format call number prefix");
//        }
//    }

}
