package capstone.library.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeePolicyDto {

    private Integer id;

    private Double overdueFinePerDay;

    private Integer maxPercentageOverdueFine;

    private Double documentProcessing_Fee;

    private Integer missingDocMultiplier;

    private LocalDateTime createdAt;
}
