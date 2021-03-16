package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddPortableSearchingBooksRequest {

    private int patronId;

    private List<Integer> bookIdList;
}
