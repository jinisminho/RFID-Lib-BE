package capstone.library.demo.entities;

import capstone.library.demo.enums.Gender;
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
@Table(name = "profile")
public class Profile {

    @Id
    private Integer id;

    @Column(name = "fullname", length = 50, nullable = false)
    private String fullName;

    @Column(name = "phone", length = 10, nullable = false)
    private String phone;

    @Column(name = "gender", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    @MapsId
    private Account account;
}
