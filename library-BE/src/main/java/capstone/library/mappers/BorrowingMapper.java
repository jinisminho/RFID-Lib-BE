package capstone.library.mappers;

import capstone.library.dtos.response.BorrowingResDto;
import capstone.library.entities.Borrowing;
import org.mapstruct.Mapper;

@Mapper(uses = {BookBorrowingMapper.class, BookMapper.class, AccountMapper.class, BookCopyMapper.class}, componentModel = "spring")
public interface BorrowingMapper {
    BorrowingResDto toDto(Borrowing entity);
}
