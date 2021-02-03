package capstone.library.services.impl;

import capstone.library.dtos.response.AuthorResponseDto;
import capstone.library.entities.Author;
import capstone.library.repositories.AuthorRepository;
import capstone.library.services.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService
{
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    ObjectMapper objectMapper;


    @Override
    public List<AuthorResponseDto> findAllAuthors()
    {
        List<Author> authors = authorRepository.findAll();
        List<AuthorResponseDto> response = new ArrayList<>();
        for (Author author : authors)
        {
            response.add(objectMapper.convertValue(author, AuthorResponseDto.class));
        }
        return response;
    }
}
