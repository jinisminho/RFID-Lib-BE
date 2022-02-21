package capstone.library.mappers;

import capstone.library.dtos.common.BookCopyTypeDto;
import capstone.library.dtos.common.BorrowPolicyDto;
import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.entities.BookCopyType;
import capstone.library.entities.BorrowPolicy;
import capstone.library.entities.PatronType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowPolicyMapper {

//    BorrowPolicyMapper INSTANCE = Mappers.getMapper(BorrowPolicyMapper.class);

    BorrowPolicy toEntity(BorrowPolicyDto dto);

    BorrowPolicyDto toDto(BorrowPolicy entity);

    //Mapper for types
    //Patron
    PatronTypeDto toPatTypeDto(PatronType entity);

    BookCopyTypeDto toCpyTypeDto(BookCopyType entity);

}
