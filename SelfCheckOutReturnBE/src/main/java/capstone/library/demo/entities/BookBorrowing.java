package capstone.library.demo.entities;


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

    @Column(name = "due_at", nullable = false)
    private LocalDate dueAt;

    @Column(name = "extended_at")
    private LocalDateTime extendedAt;

    @Column(name = "extend_index")
    private int extendIndex = 0;

    @Column(name = "lost_at")
    private LocalDateTime lostAt;

    @Column(name = "fine")
    private double fine = 0;

    @Column(name = "note")
    private String note;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private Account issued_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by")
    private Account return_by;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_policy_id")
    private FeePolicy feePolicy;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "borrow_id")
    private Borrowing borrowing;

}
