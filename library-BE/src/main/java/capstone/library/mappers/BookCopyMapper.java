package capstone.library.mappers;

import capstone.library.dtos.common.ProfileDto;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.entities.BookCopy;
import capstone.library.entities.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface BookCopyMapper {

//    BookCopyMapper INSTANCE = Mappers.getMapper(BookCopyMapper.class);

    @Mapping(target = "account", ignore = true)
    ProfileDto toProfileDto(Profile entity);

//    BookCopy toEntity(BookCopyDto dto);
//
//    BookCopyDto toDto(BookCopy entity);

    BookCopyResDto toResDto(BookCopy entity);

}
