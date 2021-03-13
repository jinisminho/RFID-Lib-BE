package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadPDFResponse {

    private Resource resource;

    private String title;

    private Integer edition;

    private String type;

    private Double price;

}
