package capstone.library.mappers;

import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.entities.BookBorrowing;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookBorrowingMapper {

    BookBorrowingMapper INSTANCE = Mappers.getMapper(BookBorrowingMapper.class);

    BookBorrowing toEntity(BookBorrowingResDto dto);

    BookBorrowingResDto toDto(BookBorrowing entity);
}