package capstone.library.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "security_deactivated_copy")
public class SecurityDeactivatedCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "rfid", length = 80)
    private String rfid;

    public SecurityDeactivatedCopy(String rfid) {
        this.rfid = rfid;
    }
}
