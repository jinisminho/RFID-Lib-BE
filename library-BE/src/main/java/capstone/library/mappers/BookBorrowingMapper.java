package capstone.library.mappers;

import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.entities.BookBorrowing;
import org.mapstruct.Mapper;

@Mapper(uses = {BookMapper.class, AccountMapper.class}, componentModel = "spring")
public interface BookBorrowingMapper {

//    BookBorrowingMapper INSTANCE = Mappers.getMapper(BookBorrowingMapper.class);

//    BookBorrowing toEntity(BookBorrowingResDto dto);

    BookBorrowingResDto toDto(BookBorrowing entity);
}