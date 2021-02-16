package capstone.library.services.impl;

import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyPosition;
import capstone.library.entities.BookCopyType;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyPositionRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.services.BookCopyPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCopyPositionServiceImpl implements BookCopyPositionService {

    @Autowired
    BookCopyRepository bookCopyRepository;

    @Autowired
    BookCopyPositionRepository positionRepository;


    @Override
    public BookCopyPositionResponse findPositionForBookCopy(int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Copy",
                        "Cannot find copy with id:" + bookCopyId));
        String searchCallNumber = bookCopy.getBook().getCallNumber();
        return findPositionOfBookCopy(searchCallNumber, bookCopy.getBookCopyType());
    }

    @Override
    public List<BookCopyPositionResponse> findPositionForBook(int bookId) {
        return null;
    }

    private BookCopyPositionResponse findPositionOfBookCopy(String callNumber, BookCopyType bookCopyType){
        List<BookCopyPosition> positions = positionRepository
                .findByBookCopyTypeIdOrderByFromCallNumberAsc(bookCopyType.getId());
        try {
            int callNumberPrefix = Integer.parseInt(callNumber.substring(0, 3));
            if (!positions.isEmpty()) {
                BookCopyPosition holdingPosition = positions.get(0);
                for (BookCopyPosition position : positions) {
                    int tmpCallNumber = Integer.parseInt(position.getFromCallNumber());
                    if (tmpCallNumber > callNumberPrefix) {
                        break;
                    }else{
                        holdingPosition = position;
                    }
                }
                return new BookCopyPositionResponse(bookCopyType.getName()
                        , callNumber, holdingPosition.getFloor() + "", holdingPosition.getShelf());

            } else {
                return new BookCopyPositionResponse(bookCopyType.getName(), callNumber, "Not found", "Not found");
            }
        }catch (NumberFormatException e){
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Number Format Exception", "Cannot format call number prefix");
        }
    }

}
