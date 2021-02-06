package capstone.library.services.impl;

import capstone.library.dtos.response.BookCopyTypeResponseDto;
import capstone.library.entities.BookCopyType;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.services.BookCopyTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import capstone.library.dtos.common.BookCopyTypeDto;
import java.util.stream.Collectors;

@Service
public class BookCopyTypeServiceImpl implements BookCopyTypeService
{
    @Autowired
    BookCopyTypeRepository bookCopyTypeRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<BookCopyTypeResponseDto> getAllCopyTypes()
    {
        List<BookCopyTypeResponseDto> response = new ArrayList<>();
        for (BookCopyType bookCopyType : bookCopyTypeRepository.findAll())
        {
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
}