package capstone.library.demo.entities;

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
@Table(name = "book_copy_position")
public class BookCopyPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "floor", nullable = false)
    private int floor;

    @Column(name = "shelf", length = 50, nullable = false)
    private String shelf;

    @Column(name = "from_call_number", length = 50, nullable = false)
    private Integer fromCallNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_type_id")
    private BookCopyType bookCopyType;

}
