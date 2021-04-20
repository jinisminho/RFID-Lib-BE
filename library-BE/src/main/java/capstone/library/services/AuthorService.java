package capstone.library.services;

import capstone.library.dtos.request.CreateAuthorReqDto;
import capstone.library.dtos.response.AuthorResponseDto;

import java.util.List;

public interface AuthorService {
    List<AuthorResponseDto> findAllAuthors();

    boolean addAuthor(CreateAuthorReqDto reqDto);

    boolean updateAuthor(int id, CreateAuthorReqDto reqDto);

    boolean deleteAuthor(int id);
}
