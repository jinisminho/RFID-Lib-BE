package capstone.library.services.impl;

import capstone.library.dtos.BookDto;
import capstone.library.repositories.BookRepository;
import capstone.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BookDto> findBooks(String searchValue) {
        List<BookDto> res = new ArrayList<>();


        return res;
    }

}
