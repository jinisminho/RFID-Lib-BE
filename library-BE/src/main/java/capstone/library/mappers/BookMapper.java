package capstone.library.mappers;

import capstone.library.dtos.response.AuthorResDto;
import capstone.library.dtos.response.BookResDto;
import capstone.library.dtos.response.GenreResDto;
import capstone.library.entities.Book;
import capstone.library.entities.BookAuthor;
import capstone.library.entities.BookGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mappings({
            @Mapping(target = "id", source = "author.id"),
            @Mapping(target = "name", source = "author.name"),
    })
    AuthorResDto toAuthorDto(BookAuthor entity);

    Set<AuthorResDto> toAuthorDtos(Set<BookAuthor> entities);

    @Mappings({
            @Mapping(target = "id", source = "genre.id"),
            @Mapping(target = "name", source = "genre.name"),
    })
    GenreResDto toCategoryDto(BookGenre entity);

    Set<GenreResDto> toCategoryDtos(Set<BookGenre> entities);

    @Mappings({
            @Mapping(target = "genre", source = "bookGenres"),
            @Mapping(target = "author", source = "bookAuthors")
    })
    BookResDto toResDto(Book entity);

}

