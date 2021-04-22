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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Page<AuthorResponseDto> search(String name, String country, Integer birthYear, Pageable pageable) {
        if (name != null && !name.isEmpty() && country != null && !country.isEmpty() && birthYear != null) {
            Page<Author> page = authorRepository.findByNameLikeAndCountryAndBirthYear("%" + name + "%", country, birthYear, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (name != null && !name.isEmpty() && country != null && !country.isEmpty()) {
            Page<Author> page = authorRepository.findByNameLikeAndCountry("%" + name + "%", country, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (name != null && !name.isEmpty() && birthYear != null) {
            Page<Author> page = authorRepository.findByNameLikeAndBirthYear("%" + name + "%", birthYear, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (name != null && !name.isEmpty()) {
            Page<Author> page = authorRepository.findByNameLike("%" + name + "%", pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (country != null && !country.isEmpty() && birthYear != null) {
            Page<Author> page = authorRepository.findByCountryAndBirthYear(country, birthYear, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (country != null && !country.isEmpty()) {
            Page<Author> page = authorRepository.findByCountry(country, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else if (birthYear != null) {
            Page<Author> page = authorRepository.findByBirthYear(birthYear, pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        } else {
            Page<Author> page = authorRepository.findAll(pageable);
            return new PageImpl<AuthorResponseDto>(page.map(author -> objectMapper.convertValue(author, AuthorResponseDto.class)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        }
    }
}
