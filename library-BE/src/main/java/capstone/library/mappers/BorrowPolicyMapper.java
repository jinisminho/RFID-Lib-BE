package capstone.library.mappers;

import capstone.library.dtos.common.BorrowPolicyDto;
import capstone.library.entities.BorrowPolicy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowPolicyMapper {

//    BorrowPolicyMapper INSTANCE = Mappers.getMapper(BorrowPolicyMapper.class);

    BorrowPolicy toEntity(BorrowPolicyDto dto);

    BorrowPolicyDto toDto(BorrowPolicy entity);

}
