package capstone.library.entities;

import capstone.library.enums.LostBookStatus;
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
public class BookLostReport
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lost_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime lostAt;

    @Column(name = "reason", length = 100)
    private String reason;

    @Column(name = "fine")
    private Double fine;

    @Column(name = "status", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private LostBookStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_id")
    private BookBorrowing bookBorrowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private Account librarian;

}
