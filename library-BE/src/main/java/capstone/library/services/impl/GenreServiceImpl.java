package capstone.library.services.impl;

import capstone.library.dtos.response.GenreResponseDto;
import capstone.library.entities.Genre;
import capstone.library.repositories.GenreRepository;
import capstone.library.services.GenreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GenreServiceImpl implements GenreService
{
    @Autowired
    GenreRepository genreRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<GenreResponseDto> findAllGenres()
    {
        List<Genre> genres = genreRepository.findAll();
        List<GenreResponseDto> response = new ArrayList<>();
        for (Genre genre : genres)
        {
            response.add(objectMapper.convertValue(genre, GenreResponseDto.class));
        }
        return response;
    }
}
