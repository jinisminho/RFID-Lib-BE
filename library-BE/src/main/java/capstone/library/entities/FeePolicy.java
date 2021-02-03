package capstone.library.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_policy")
@JsonIgnoreProperties(
        value = {"createdAt"},
        allowGetters = true
)
public class FeePolicy
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "overdue_fine_per_day")
    private double overdueFinePerDay;

    @Column(name = "max_percentage_overdue_fine")
    private int maxPercentageOverdueFine;

    @Column(name = "document_processing_fee")
    private double documentProcessing_Fee;

    @Column(name = "missing_doc_multiplier")
    private int missingDocMultiplier;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
}
