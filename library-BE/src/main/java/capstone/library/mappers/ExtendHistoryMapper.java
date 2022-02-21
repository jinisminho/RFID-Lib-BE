package capstone.library.mappers;

import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.entities.ExtendHistory;
import org.mapstruct.Mapper;

@Mapper(uses = AccountMapper.class, componentModel = "spring")
public interface ExtendHistoryMapper {

//    ExtendHistoryMapper INSTANCE = Mappers.getMapper(ExtendHistoryMapper.class);

//    ExtendHistory toEntity(ExtendHistoryResDto dto);


    ExtendHistoryResDto toResDto(ExtendHistory entity);

}
