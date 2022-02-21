package capstone.library.mappers;

import capstone.library.dtos.response.AuthorResDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.entities.Book;
import capstone.library.entities.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface BookMapper {

//    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mappings({
            @Mapping(target = "id", source = "author.id"),
            @Mapping(target = "name", source = "author.name"),
    })
    AuthorResDto toAuthorDto(BookAuthor entity);

    Set<AuthorResDto> toAuthorDtos(Set<BookAuthor> entities);

    @Mappings({
            @Mapping(target = "author", source = "bookAuthors"),
    })
    BookResDto toResDto(Book entity);

}

