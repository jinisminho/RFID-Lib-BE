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
@Table(name = "book_position")
public class BookPosition extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "shelf", length = 10, nullable = false)
    private String shelf;

    @Column(name = "line", nullable = false)
    private Integer price;

}
