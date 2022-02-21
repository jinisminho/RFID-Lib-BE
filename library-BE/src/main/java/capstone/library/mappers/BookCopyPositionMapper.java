package capstone.library.mappers;

import capstone.library.dtos.common.PositionDto;
import capstone.library.dtos.request.CreateCopyPostionReqDto;
import capstone.library.entities.BookCopyPosition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface BookCopyPositionMapper {
    BookCopyPosition toEntity(CreateCopyPostionReqDto createDto);

    BookCopyPosition toEntity(PositionDto dto);

    PositionDto toDto(BookCopyPosition entity);
}
