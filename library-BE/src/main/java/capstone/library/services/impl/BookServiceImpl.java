package capstone.library.services.impl;

import capstone.library.dtos.BookDto;
import capstone.library.services.BookService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    

    @Override
    public List<BookDto> findBooks(String searchValue) {
        List<BookDto> res = new ArrayList<>();


        return res;
    }

}
