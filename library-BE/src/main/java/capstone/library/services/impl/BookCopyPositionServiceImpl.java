package capstone.library.services.impl;

import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyPosition;
import capstone.library.entities.BookCopyType;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyPositionRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        return findPositionOfBookCopy(searchCallNumber, bookCopy.getBookCopyType());
    }

    @Override
    public List<BookCopyPositionResponse> findPositionForBook(int bookId) {
        List<BookCopyPositionResponse> rs = new ArrayList<>();
        List<BookCopy> bookCopyList = bookCopyRepository.findByBookId(bookId);
        if(!bookCopyList.isEmpty()){

            String callNumber = bookCopyList.get(0).getBook().getCallNumber();

            Map<Integer, Long> groupCopyType = bookCopyList.stream()
                    .collect(Collectors.groupingBy(b -> b.getBookCopyType().getId(), Collectors.counting()));

            groupCopyType.forEach(
                    (k, v) -> {
                        BookCopyPositionResponse tmp;
                        BookCopyType copyType = bookCopyTypeRepository.findById(k)
                                .orElseThrow(() -> new ResourceNotFoundException("Book Copy Type",
                                        "Cannot find book copy type with id: " + k));
                        tmp = findPositionOfBookCopy(callNumber, copyType);
                        rs.add(tmp);
                    }
            );
        }
        return rs;
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
