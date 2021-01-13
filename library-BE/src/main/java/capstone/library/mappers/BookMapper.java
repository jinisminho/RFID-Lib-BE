package capstone.library.mappers;

import capstone.library.dtos.BookDto;
import capstone.library.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookDto toDto(Book entity);

}
