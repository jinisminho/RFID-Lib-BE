package capstone.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.*;
import java.util.Set;

@Indexed
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Book extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @KeywordField
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @FullTextField
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "publish_year", nullable = false)
    private Integer publishYear;

    @Column(name = "edition", nullable = false)
    private Integer edition;

    @Column(name = "language", length = 20, nullable = false)
    private String language;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(name = "ddc", length = 20, nullable = false)
    private String ddc;

    @Column(name = "number_of_copy", nullable = false)
    private Integer numberOfCopy;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "book")
    public Set<BookAuthor> bookAuthors;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "book")
    public Set<BookCategory> bookCategories;


    @Override
    public String toString() {
        return bookAuthors.toString();
    }
}
