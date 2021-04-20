package capstone.library.mappers;

import capstone.library.dtos.common.AuthorDto;
import capstone.library.dtos.request.CreateAuthorReqDto;
import capstone.library.entities.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

//    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    Author toEntity(CreateAuthorReqDto createDto);

    Author toEntity(AuthorDto dto);

    AuthorDto toDto(Author entity);


}