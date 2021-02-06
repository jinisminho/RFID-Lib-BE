package capstone.library.services.impl;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCopyTypeServiceImpl implements BookCopyTypeService {

    @Autowired
    BookCopyTypeRepository bookCopyTypeRepo;

    @Autowired
    ObjectMapper mapper;

    @Override
    public List<BookCopyTypeDto> getAllBookCopyType() {
        return bookCopyTypeRepo
                .findAll()
                .stream()
                .map(b -> mapper.convertValue(b, BookCopyTypeDto.class))
                .collect(Collectors.toList());
    }
}
