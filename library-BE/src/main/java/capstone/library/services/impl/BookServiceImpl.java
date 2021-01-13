package capstone.library.services.impl;

import capstone.library.dtos.BookDto;
import capstone.library.mappers.BookMapper;
import capstone.library.repositories.BookRepository;
import capstone.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BookDto> findBooks(String searchValue) {
        return bookRepository.findBooks(searchValue).stream().map(book -> BookMapper.INSTANCE.toDto(book)).collect(Collectors.toList());
    }

}
