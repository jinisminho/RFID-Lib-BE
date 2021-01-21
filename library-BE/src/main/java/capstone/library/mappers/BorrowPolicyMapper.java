package capstone.library.mappers;

import capstone.library.dtos.common.BorrowPolicyDto;
import capstone.library.entities.BorrowPolicy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BorrowPolicyMapper {

    BorrowPolicyMapper INSTANCE = Mappers.getMapper(BorrowPolicyMapper.class);

    BorrowPolicy toEntity(BorrowPolicyDto dto);

    BorrowPolicyDto toDto(BorrowPolicy entity);

}
