package capstone.library.entities;

import capstone.library.enums.BookCopyStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copy")
public class BookCopy extends Audit
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "barcode", length = 100, nullable = false)
    private String barcode;

    @Column(name = "rfid", length = 80, nullable = false)
    private String rfid;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

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
}
