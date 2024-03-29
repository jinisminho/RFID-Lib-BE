package capstone.library.entities;

import capstone.library.enums.BookStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.engine.backend.types.Norms;
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

    @Column(name = "img", length = 500)
    private String img;

    @KeywordField
    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @FullTextField(name = "title", analyzer = "my", norms = Norms.NO)
    @FullTextField(name = "title_2", norms = Norms.NO)
    @Column(name = "title", nullable = false)
    private String title;

    @FullTextField(name = "sub", analyzer = "my", norms = Norms.NO)
    @FullTextField(name = "sub_2", norms = Norms.NO)
    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "publish_year", nullable = false)
    private Integer publishYear;

    @Column(name = "edition", nullable = false)
    private Integer edition;

    @Column(name = "language", length = 20)
    private String language;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "call_number", length = 30, nullable = false)
    private String callNumber;

    @Column(name = "number_of_copy", nullable = false)
    private Integer numberOfCopy;

    @Column(name = "status", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    @KeywordField
    private BookStatus status;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "book")
    public Set<BookAuthor> bookAuthors;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "book")
    public Set<BookCopy> bookCopies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Account creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Account updater;


    @Override
    public String toString() {
        return bookAuthors.toString();
    }
}
