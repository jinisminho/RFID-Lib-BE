package capstone.library.services.impl;

import capstone.library.dtos.common.AuthorDto;
import capstone.library.dtos.request.CreateAuthorReqDto;
import capstone.library.dtos.response.AuthorResponseDto;
import capstone.library.entities.Author;
import capstone.library.exceptions.InvalidRequestException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.mappers.AuthorMapper;
import capstone.library.repositories.AuthorRepository;
import capstone.library.repositories.BookJpaRepository;
import capstone.library.services.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    private BookJpaRepository bookJpaRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AuthorMapper authorMapper;

    @Override
    public List<AuthorResponseDto> findAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        List<AuthorResponseDto> response = new ArrayList<>();
        for (Author author : authors) {
            response.add(objectMapper.convertValue(author, AuthorResponseDto.class));
        }
        return response;
    }

    @Override
    @Transactional
    public boolean addAuthor(CreateAuthorReqDto reqDto) {
        Author newAuthor = authorMapper.toEntity(reqDto);

        authorRepository.save(newAuthor);

        return true;
    }

    @Override
    @Transactional
    public boolean updateAuthor(int id, CreateAuthorReqDto reqDto) {
        Optional<Author> authorOpt = authorRepository.findById(id);

        if (authorOpt.isPresent()) {
            AuthorDto updateAuthor = authorMapper.toDto(authorOpt.get());
            updateAuthor.setId(id);
            if (reqDto.getName() != null && !reqDto.getName().isEmpty())
                updateAuthor.setName(reqDto.getName());
            if (reqDto.getCountry() != null && !reqDto.getCountry().isEmpty())
                updateAuthor.setCountry(reqDto.getCountry());
            if (reqDto.getBirthYear() != null)
                updateAuthor.setBirthYear(reqDto.getBirthYear());
            authorRepository.save(authorMapper.toEntity(updateAuthor));
            return true;
        } else {
            throw new ResourceNotFoundException("Author", "Author [" + id + "] is not found");
        }

    }

    @Override
    @Transactional
    public boolean deleteAuthor(int id) {
        Optional<Author> authorOpt = authorRepository.findById(id);

        if (authorOpt.isPresent()) {
            if (authorOpt.get().getBookAuthors().isEmpty()) {
                authorRepository.delete(authorOpt.get());
                return true;
            } else {
                throw new InvalidRequestException("Can not delete a in-use author");
            }
        } else {
            throw new ResourceNotFoundException("Author", "Author [" + id + "] is not found");
        }
    }
}
