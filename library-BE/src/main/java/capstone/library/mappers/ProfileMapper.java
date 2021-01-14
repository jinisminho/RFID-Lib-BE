package capstone.library.mappers;

import capstone.library.dtos.ProfileDto;
import capstone.library.entities.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {

    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    Profile toEntity(ProfileDto dto);

    ProfileDto toDto(Profile entity);
}