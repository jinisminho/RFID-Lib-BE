package capstone.library.services;

import capstone.library.dtos.response.AuthorResponseDto;

import java.util.List;

public interface AuthorService
{
    List<AuthorResponseDto> findAllAuthors();
}
