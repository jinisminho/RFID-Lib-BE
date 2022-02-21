package capstone.library.mappers;

import capstone.library.dtos.common.ProfileDto;
import capstone.library.dtos.response.BookCopyResDto;
import capstone.library.dtos.response.CopyResponseDto;
import capstone.library.entities.BookAuthor;
import capstone.library.entities.BookCopy;
import capstone.library.entities.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface BookCopyMapper {

//    BookCopyMapper INSTANCE = Mappers.getMapper(BookCopyMapper.class);

    @Mapping(target = "account", ignore = true)
    ProfileDto toProfileDto(Profile entity);

//    BookCopy toEntity(BookCopyDto dto);
//
//    BookCopyDto toDto(BookCopy entity);

    BookCopyResDto toResDto(BookCopy entity);


    @Mappings({
            @Mapping(target = "borrowing.bookBorrowings", ignore = true),
            @Mapping(target = "copyType", source = "bookCopyType.name"),
            @Mapping(target = "book.barcode", source = "barcode"),
            @Mapping(target = "book.rfid", source = "rfid"),
            @Mapping(target = "book", source = "book"),
            @Mapping(target = "book.authors", source = "book.bookAuthors"),
    })
    CopyResponseDto toResAltDto(BookCopy entity);

    default String asString(Set<BookAuthor> authors) {
        String res = "";
        List<BookAuthor> bookAuthors = authors.stream().collect(Collectors.toList());
        if (bookAuthors != null && bookAuthors.size() > 0) {
            for (int i = 0; i < bookAuthors.size(); i++) {
                if (i == 0) res += bookAuthors.get(i).getAuthor().getName();
                else res += ", " + bookAuthors.get(i).getAuthor().getName();
            }
        }
        return !res.isEmpty() ? res : null;
    }

}
