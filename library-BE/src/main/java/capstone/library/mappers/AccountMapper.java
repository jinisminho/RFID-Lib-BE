package capstone.library.mappers;

import capstone.library.dtos.response.ProfileAccountResDto;
import capstone.library.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AccountMapper {

//    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mappings({
            @Mapping(target = "profileId", source = "profile.id"),
            @Mapping(target = "fullName", source = "profile.fullName"),
            @Mapping(target = "phone", source = "profile.phone"),
            @Mapping(target = "gender", source = "profile.gender"),
            @Mapping(target = "accountId", source = "id"),
            @Mapping(target = "role", source = "role.name"),
    })
    ProfileAccountResDto toProfileAccountResDto(Account entity);

}
