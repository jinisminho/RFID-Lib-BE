package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.request.AddBookCopyTypeReqDto;
import capstone.library.dtos.request.BookCopyTypeReqDto;
import capstone.library.dtos.response.BookCopyTypeResponseDto;
import capstone.library.entities.BookCopy;
import capstone.library.entities.BookCopyType;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyRepository;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookCopyTypeServiceImpl implements BookCopyTypeService {
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    BookCopyRepository bookCopyRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<BookCopyTypeResponseDto> getAllCopyTypes() {
        List<BookCopyTypeResponseDto> response = new ArrayList<>();
        for (BookCopyType bookCopyType : bookCopyTypeRepository.findAll()) {
            response.add(objectMapper.convertValue(bookCopyType, BookCopyTypeResponseDto.class));
        }
        return response;
    }

    @Override
    public List<BookCopyTypeDto> getAllBookCopyType() {
        return bookCopyTypeRepository
                .findAll()
                .stream()
                .map(b -> objectMapper.convertValue(b, BookCopyTypeDto.class))
                .collect(Collectors.toList());
    }

    //
    @Override
    public boolean addBookCopyType(AddBookCopyTypeReqDto req) {
        boolean isAdded = false;

        BookCopyType bookCopyType = new BookCopyType();

        if (req.getName() != null && !req.getName().isEmpty()) {
            bookCopyType.setName(req.getName());
            isAdded = true;
        }

        bookCopyTypeRepository.save(bookCopyType);

        return isAdded;
    }

    //
    @Override
    public boolean updateBookCopyType(Integer id, BookCopyTypeReqDto req) {
        boolean isUpdated = false;

        BookCopyType bookCopyType = bookCopyTypeRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy Type"
                        , "Cannot find BookCopy type with id: " + id));

        if (req.getName() != null && !req.getName().isEmpty()) {
            bookCopyType.setName(req.getName());
            isUpdated = true;
        }

        bookCopyTypeRepository.save(bookCopyType);

        return isUpdated;
    }

    //
    @Override
    public boolean deleteBookCopyType(Integer id) {
        BookCopyType bookCopyType = bookCopyTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy Type",
                        "Cannot find BookCopy type with id: " + id));
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.getTopByBookCopyType_Id(id);
        if (bookCopyOpt.isPresent()) throw new InvalidRequestException("There is book copy using this book copy type.");
        bookCopyTypeRepository.delete(bookCopyType);
        return true;
    }

    //
    @Override
    public Page<BookCopyTypeDto> getPatronType(Pageable pageable, String name) {
        Page<BookCopyType> rs;
        if (name != null) {
            rs = bookCopyTypeRepository.findByNameContains(pageable, name);
        } else {
            rs = bookCopyTypeRepository.findAll(pageable);
        }

        return rs.map(p -> objectMapper.convertValue(p, BookCopyTypeDto.class));
    }
}