package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportPatronResponse {

    private List<ImportPatron> importPatronList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportPatron{

        private String email;

        private String rawPassword;
    }
}
