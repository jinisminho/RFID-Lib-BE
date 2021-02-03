package capstone.library.entities;

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
@Table(name = "borrow_policy")
public class BorrowPolicy extends Audit
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "due_duration", nullable = false)
    private int dueDuration;

    @Column(name = "max_borrow_number", nullable = false)
    private int maxNumberCopyBorrow;

    @Column(name = "max_extend_time", nullable = false)
    private int maxExtendTime;

    @Column(name = "extend_due_duration", nullable = false)
    private int extendDueDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_type_id")
    private PatronType patronType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_type_id")
    private BookCopyType bookCopyType;
}
