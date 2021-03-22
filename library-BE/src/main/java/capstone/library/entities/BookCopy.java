package capstone.library.entities;

import capstone.library.enums.BookCopyStatus;
import capstone.library.util.tools.DoubleFormatter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.*;

@Indexed
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copy")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class BookCopy extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @KeywordField
    @Column(name = "barcode", length = 100, nullable = false)
    private String barcode;

    @KeywordField
    @Column(name = "rfid", length = 80, nullable = false)
    private String rfid;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    @KeywordField
    private BookCopyStatus status;

    @IndexedEmbedded
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_type_id")
    private BookCopyType bookCopyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Account creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private Account updater;

    @Transient
    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_position_id")
    private BookCopyPosition bookCopyPosition;

    public String generateStringBarcode(){
        return this.book.getIsbn() + "-" + this.bookCopyType.getName() + "-" + DoubleFormatter.formatToDecimal(this.price);
    }

}
