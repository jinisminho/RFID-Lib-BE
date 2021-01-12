package capstone.library.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "book_lost_report")
public class BookLostReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "lost_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime lostAt;

    @Column(name = "reason", length = 100, nullable = false)
    private String reason;

    private BookBorrowing bookBorrowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_by")
    private Account borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private Account librarian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

}
