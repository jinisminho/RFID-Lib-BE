package capstone.library.services;

import capstone.library.dtos.response.GenreResponseDto;

import java.util.List;

public interface GenreService
{
    List<GenreResponseDto> findAllGenres();
}
