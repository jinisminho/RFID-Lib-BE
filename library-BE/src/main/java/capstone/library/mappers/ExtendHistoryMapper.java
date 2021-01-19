package capstone.library.mappers;

import capstone.library.dtos.response.ExtendHistoryResDto;
import capstone.library.entities.ExtendHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExtendHistoryMapper {

    ExtendHistoryMapper INSTANCE = Mappers.getMapper(ExtendHistoryMapper.class);

    ExtendHistory toEntity(ExtendHistoryResDto dto);

    ExtendHistoryResDto toDto(ExtendHistory entity);

}
