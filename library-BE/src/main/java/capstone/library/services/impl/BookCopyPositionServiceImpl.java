package capstone.library.services.impl;

import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyPosition;
import capstone.library.entities.BookCopyType;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyPositionRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyPositionService;
import capstone.library.util.tools.DoubleFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookCopyPositionServiceImpl implements BookCopyPositionService {

    @Autowired
    BookCopyRepository bookCopyRepository;

    @Autowired
    BookCopyPositionRepository positionRepository;

    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;


    @Override
    public BookCopyPositionResponse findPositionForBookCopy(int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Copy",
                        "Cannot find copy with id:" + bookCopyId));
        String searchCallNumber = bookCopy.getBook().getCallNumber();
        BookCopyPosition position = bookCopy.getBookCopyPosition();
        if(position == null){
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
        if(!bookCopyList.isEmpty()){
            for(BookCopy copy : bookCopyList){
                BookCopyPosition position = copy.getBookCopyPosition();
                BookCopyPositionResponse response = new BookCopyPositionResponse();
                response.setBookCopyType(copy.getBookCopyType().getName());
                response.setCallNumber(copy.getBook().getCallNumber());
                response.setStatus(copy.getStatus().toString());
                if(position != null){
                    response.setLine(position.getLine().toString());
                    response.setShelf(position.getShelf());
                }else{
                    response.setLine("N/A");
                    response.setShelf("N/A");
                }
                rs.add(response);
            }

        }
        return rs;
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
