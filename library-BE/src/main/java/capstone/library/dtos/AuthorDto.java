package capstone.library.dtos;

import capstone.library.entities.BookAuthor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    private int id;

    @Column(name = "name", length = 100, nullable = false)
    @NotNull(message = "{book.isbn.notNull}")
    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "author")
    public Set<BookAuthor> bookAuthor;

}
