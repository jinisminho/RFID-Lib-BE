package capstone.library.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_borrowing")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class BookBorrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "due_at", nullable = false, columnDefinition = "DATE")
    private LocalDate dueAt;

    @Column(name = "extended_at")
    private LocalDateTime extendedAt;

    @Column(name = "extend_index")
    private int extendIndex;

    @Column(name = "lost_at")
    private LocalDateTime lostAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private Account issued_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by")
    private Account return_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @Column(name = "fine")
    private Double fine = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_policy_id")
    private FeePolicy feePolicy;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "borrow_id")
    private Borrowing borrowing;
}
