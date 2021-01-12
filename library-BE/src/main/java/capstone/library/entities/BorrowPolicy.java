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
public class BorrowPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="due_duration",nullable = false)
    private int dueDuration;

    @Column(name="max_number_copy_borrow",nullable = false)
    private int maxNumberCopyBorrow;

    @Column(name = "max_extend_time",nullable = false)
    private int maxExtendTime;

    @Column(name = "extend_due_duration",nullable = false)
    private int extendDueDuration;

    @Column(name = "overdue_fine_per_day",nullable = false)
    private double overdueFinePerDay;

    @Column(name = "policy_form_url")
    private String policyFormUrl;
}
