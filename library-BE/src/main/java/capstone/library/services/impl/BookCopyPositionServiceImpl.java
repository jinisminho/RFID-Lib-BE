package capstone.library.services.impl;

import capstone.library.dtos.common.PositionDto;
import capstone.library.dtos.request.CreateCopyPostionReqDto;
import capstone.library.dtos.request.SaveSamplePositionRequestDto;
import capstone.library.dtos.response.BookCopyPositionResponse;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyPosition;
import capstone.library.enums.BookCopyStatus;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.BookCopyPositionMapper;
import capstone.library.repositories.AccountRepository;
import capstone.library.repositories.BookCopyPositionRepository;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyPositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

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
    @Autowired
    BookCopyPositionMapper bookCopyPositionMapper;

    private static final String SUCCESS_MESSAGE = "Success";
    private static final String BOOK_COPY = "Book copy";
    private static final String POSITION = "Book copy position";
    private static final String UPDATER = "Account";
    private static final String BOOK_COPY_NOT_FOUND_ERROR = "Book copy not found: ";
    private static final String POSITION_NOT_FOUND_ERROR = "Location is not found";
    private static final String UPDATER_NOT_FOUND_ERROR = "Updater account not found: ";
    private static final String BOOK_COPY_STATUS_ERROR = "Book copy status error. Barcode: ";


    @Override
    public BookCopyPositionResponse findPositionForBookCopy(int bookCopyId) {
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book Copy",
                        "Cannot find copy with id:" + bookCopyId));
//        String searchCallNumber = bookCopy.getBook().getCallNumber();
        BookCopyPosition position = bookCopy.getBookCopyPosition();
        if (position == null) {
            return new BookCopyPositionResponse(
                    bookCopy.getBook().getCallNumber(),
                    "N/A",
                    "N/A");
        }
        return new BookCopyPositionResponse(
                bookCopy.getBook().getCallNumber(),
                position.getLine().toString(),
                position.getShelf());
    }

    /* Only return available or lib-in-use position with unique pair of shelf-line*/
    @Override
    public List<BookCopyPositionResponse> findPositionForBook(int bookId) {
        List<BookCopyPositionResponse> rs = new ArrayList<>();
        Set<BookCopyStatus> availableCopyStatus = new HashSet<>();
        availableCopyStatus.add(BookCopyStatus.AVAILABLE);
        availableCopyStatus.add(BookCopyStatus.LIB_USE_ONLY);
        List<BookCopy> availableBookCopyList = bookCopyRepository.findByBookIdAndStatusIn(bookId, availableCopyStatus);
        if (!availableBookCopyList.isEmpty()) {
            for (BookCopy copy : availableBookCopyList) {
                BookCopyPosition position = copy.getBookCopyPosition();
                BookCopyPositionResponse response = new BookCopyPositionResponse();
                response.setCallNumber(copy.getBook().getCallNumber());
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
        List<BookCopyPositionResponse> uniquePositions = rs
                .stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(BookCopyPositionResponse::toString))),
                        ArrayList::new));
        if (uniquePositions.size() == 1 && uniquePositions.get(0).getShelf().equals("N/A")) {
            return uniquePositions;
        } else {
            return uniquePositions
                    .stream()
                    .filter(b -> !b.getShelf().equals("N/A"))
                    .collect(Collectors.toList());
        }
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
    //Hoàng
    public String saveSampledPosition(SaveSamplePositionRequestDto request) {
        BookCopyPosition bookCopyPosition = positionRepository.findById(request.getPositionId()).
                orElseThrow(() -> new ResourceNotFoundException(POSITION, POSITION_NOT_FOUND_ERROR));

        /*Remove all current book copies from this position (shelf's row)
         * Then add recently scanned book copies to this position*/
        List<BookCopy> currentBooks = bookCopyRepository.findAllByBookCopyPositionId(request.getPositionId());
        for (BookCopy bookCopy : currentBooks) {
            bookCopy.setBookCopyPosition(null);
        }
        /*=======================*/

        /*Add recently scanned book copies to this position*/
        for (String rfid : request.getRfids()) {
            BookCopy bookCopy = bookCopyRepository.findByRfid(rfid).
                    orElseThrow(() -> new ResourceNotFoundException(BOOK_COPY, BOOK_COPY_NOT_FOUND_ERROR + rfid));
            if (bookCopy.getStatus().equals(BookCopyStatus.BORROWED) ||
                    bookCopy.getStatus().equals(BookCopyStatus.LOST) ||
                    bookCopy.getStatus().equals(BookCopyStatus.DISCARD)) {
                throw new InvalidRequestException(BOOK_COPY_STATUS_ERROR + bookCopy.getBarcode() + ", Status: " + bookCopy.getStatus().name());
            }
            bookCopy.setBookCopyPosition(bookCopyPosition);
            bookCopy.setUpdater(accountRepository.findById(request.getUpdater()).
                    orElseThrow(() -> new ResourceNotFoundException(UPDATER, UPDATER_NOT_FOUND_ERROR)));
            bookCopyRepository.save(bookCopy);
        }
        /*==================*/

        return SUCCESS_MESSAGE;
    }

    @Override
    public List<CopyResponseDto> getBooksOnARow(int positionId) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookCopyPositionId(positionId);
        return convertBookCopyToDto(bookCopies);
    }

    @Override
    public List<CopyResponseDto> getBooksOnARowByRFID(String rfid) {
        List<BookCopy> bookCopies = bookCopyRepository.findAllByBookCopyPositionRfid(rfid);
        return convertBookCopyToDto(bookCopies);
    }

    @Override
    public BookCopyPositionResponse getPositionByRFID(String rfid) {
        return objectMapper.convertValue(positionRepository.findByRfid(rfid).orElseThrow(
                () -> new ResourceNotFoundException(POSITION, POSITION_NOT_FOUND_ERROR)),
                BookCopyPositionResponse.class);
    }

    private List<CopyResponseDto> convertBookCopyToDto(List<BookCopy> bookCopies) {
        List<CopyResponseDto> response = new ArrayList<>();
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getStatus().equals(BookCopyStatus.LIB_USE_ONLY) ||
                    bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
                CopyResponseDto dto = objectMapper.convertValue(bookCopy, CopyResponseDto.class);
                dto.setPosition(objectMapper.convertValue(bookCopy.getBookCopyPosition(), PositionDto.class));
                dto.getBook().setAuthors(bookCopy.getBook().getBookAuthors().
                        toString().replace("]", "").replace("[", ""));
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

    @Override
    @Transactional
    public boolean addPos(CreateCopyPostionReqDto reqDto) {
        BookCopyPosition newPos = bookCopyPositionMapper.toEntity(reqDto);

        positionRepository.save(newPos);

        return true;
    }

    @Override
    @Transactional
    public boolean updatePos(int id, CreateCopyPostionReqDto reqDto) {
        Optional<BookCopyPosition> posOpt = positionRepository.findById(id);

        if (posOpt.isPresent()) {
            PositionDto updatePos = bookCopyPositionMapper.toDto(posOpt.get());
            updatePos.setId(id);
            if (reqDto.getRfid() != null && !reqDto.getRfid().isEmpty())
                updatePos.setRfid(reqDto.getRfid());
            if (reqDto.getShelf() != null && !reqDto.getShelf().isEmpty())
                updatePos.setShelf(reqDto.getShelf());
            if (reqDto.getLine() != null)
                updatePos.setLine(reqDto.getLine());
            positionRepository.save(bookCopyPositionMapper.toEntity(updatePos));
            return true;
        } else {
            throw new ResourceNotFoundException("BookCopyPosition", "BookCopyPosition [" + id + "] is not found");
        }

    }

    @Override
    @Transactional
    public boolean deletePos(int id) {
        Optional<BookCopyPosition> posOpt = positionRepository.findById(id);
        List<BookCopy> bookCopyList = bookCopyRepository.findAllByBookCopyPositionId(id);
        if (posOpt.isPresent()) {
            if (bookCopyList.isEmpty()) {
                positionRepository.delete(posOpt.get());
                return true;
            } else {
                throw new InvalidRequestException("Can not delete a in-use position");
            }
        } else {
            throw new ResourceNotFoundException("BookCopyPosition", "BookCopyPosition [" + id + "] is not found");
        }
    }

    @Override
    public Page<BookCopyPositionResponse> getAll(String shelf, Integer line, Pageable pageable) {
        if (shelf != null && !shelf.isEmpty() && line != null) {
            Page<BookCopyPosition> page = positionRepository.findAllByShelfAndLine(shelf, line, pageable);
            return new PageImpl<BookCopyPositionResponse>(page.map(bookCopyPosition -> objectMapper.convertValue(bookCopyPosition, BookCopyPositionResponse.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if ((shelf != null && !shelf.isEmpty())) {
            Page<BookCopyPosition> page = positionRepository.findAllByShelf(shelf, pageable);
            return new PageImpl<BookCopyPositionResponse>(page.map(bookCopyPosition -> objectMapper.convertValue(bookCopyPosition, BookCopyPositionResponse.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (line != null) {
            Page<BookCopyPosition> page = positionRepository.findAllByLine(line, pageable);
            return new PageImpl<BookCopyPositionResponse>(page.map(bookCopyPosition -> objectMapper.convertValue(bookCopyPosition, BookCopyPositionResponse.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else {
            Page<BookCopyPosition> page = positionRepository.findAll(pageable);
            return new PageImpl<BookCopyPositionResponse>(page.map(bookCopyPosition -> objectMapper.convertValue(bookCopyPosition, BookCopyPositionResponse.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        }
    }
}
