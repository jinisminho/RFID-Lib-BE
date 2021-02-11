package capstone.library.mappers;

import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.entities.BookBorrowing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {BookMapper.class, AccountMapper.class}, componentModel = "spring")
public interface BookBorrowingMapper {

//    BookBorrowingMapper INSTANCE = Mappers.getMapper(BookBorrowingMapper.class);

//    BookBorrowing toEntity(BookBorrowingResDto dto);

    @Mapping(target = "borrowing.bookBorrowings", ignore = true)
    BookBorrowingResDto toResDtoWithoutBookBorrowingsInBorrowing(BookBorrowing entity);
}