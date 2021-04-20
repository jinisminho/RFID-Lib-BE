package capstone.library.mappers;

import capstone.library.dtos.common.BookBorrowingDto;
import capstone.library.dtos.response.BookBorrowingResDto;
import capstone.library.entities.BookBorrowing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {BookMapper.class, AccountMapper.class, BookCopyMapper.class}, componentModel = "spring")
public interface BookBorrowingMapper {

//    BookBorrowingMapper INSTANCE = Mappers.getMapper(BookBorrowingMapper.class);

//    BookBorrowing toEntity(BookBorrowingResDto dto);


    @Mappings({
            @Mapping(target = "borrowing.bookBorrowings", ignore = true),
    })
    BookBorrowingResDto toResDtoWithoutBookBorrowingsInBorrowing(BookBorrowing entity);

    @Mappings({
            @Mapping(target = "feePolicyId", source = "feePolicy.id"),
            @Mapping(target = "borrowingId", source = "borrowing.id"),
    })
    BookBorrowingDto toDtoWithoutBookBorrowingsInBorrowing(BookBorrowing entity);
}