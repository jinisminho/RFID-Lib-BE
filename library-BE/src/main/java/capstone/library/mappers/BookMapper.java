package capstone.library.mappers;

import capstone.library.dtos.AuthorDto;
import capstone.library.dtos.BookDto;
import capstone.library.dtos.GenreDto;
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
    AuthorDto toAuthorDto(BookAuthor entity);

    Set<AuthorDto> toAuthorDtos(Set<BookAuthor> entities);

    @Mappings({
            @Mapping(target = "id", source = "genre.id"),
            @Mapping(target = "name", source = "genre.name"),
    })
    GenreDto toCategoryDto(BookGenre entity);

    Set<GenreDto> toCategoryDtos(Set<BookGenre> entities);

    @Mappings({
            @Mapping(target = "genre", source = "bookGenres"),
            @Mapping(target = "author", source = "bookAuthors")
    })
    BookDto toDto(Book entity);

}

