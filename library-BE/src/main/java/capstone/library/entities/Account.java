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
@Table(name = "account")
public class Account extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name="password", length = 100, nullable = false)
    private String password;

    @Column(name = "pin", length = 4)
    private String pin;

    @Column(name="rfid", length = 80)
    private String rfid;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    private Account creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="updated_by")
    private  Account updater;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "account", cascade = CascadeType.PERSIST)
    private Profile profile;
}
