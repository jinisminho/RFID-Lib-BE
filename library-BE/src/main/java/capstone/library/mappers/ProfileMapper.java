package capstone.library.mappers;

import capstone.library.dtos.response.ProfileAccountResDto;
import capstone.library.entities.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

//    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    @Mappings({
            @Mapping(target = "id", source = "profileId"),
            @Mapping(target = "account.id", source = "accountId"),
            @Mapping(target = "account.email", source = "email"),
            @Mapping(target = "account.rfid", source = "rfid"),
            @Mapping(target = "account.avatar", source = "avatar"),
            @Mapping(target = "account.active", source = "active"),
            @Mapping(target = "account.role.name", source = "role"),
    })
    Profile toEntity(ProfileAccountResDto dto);

    @Mappings({
            @Mapping(target = "profileId", source = "id"),
            @Mapping(target = "accountId", source = "account.id"),
            @Mapping(target = "email", source = "account.email"),
            @Mapping(target = "rfid", source = "account.rfid"),
            @Mapping(target = "avatar", source = "account.avatar"),
            @Mapping(target = "active", source = "account.active"),
            @Mapping(target = "role", source = "account.role.name"),
    })
    ProfileAccountResDto toResDto(Profile entity);
}