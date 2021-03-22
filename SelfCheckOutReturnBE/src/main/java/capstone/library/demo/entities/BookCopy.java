package capstone.library.demo.entities;

import capstone.library.demo.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_copy")
public class BookCopy extends Audit{

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
    @JoinColumn(name = "book_copy_position_id")
    private BookCopyPosition bookCopyPosition;



}
