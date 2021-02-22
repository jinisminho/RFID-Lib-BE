package capstone.library.mappers;

import capstone.library.dtos.response.WishlistResDto;
import capstone.library.entities.WishlistBook;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BookMapper.class, AccountMapper.class})
public interface WishlistMapper {


//    WishlistBook toEntity(WishlistResDto dto);

    @Mapping(target = "patron", source = "borrower")
    WishlistResDto toResDto(WishlistBook entity);

}
