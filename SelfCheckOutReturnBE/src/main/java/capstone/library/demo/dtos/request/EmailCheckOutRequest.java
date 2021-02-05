package capstone.library.demo.dtos.request;

import capstone.library.demo.dtos.response.BookCheckOutResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NotNull(message = "{EmailCheckOutRequest.body.notNull}")
public class EmailCheckOutRequest {

    @NotNull(message = "{EmailCheckOutRequest.patronId.notNull}")
    private int patronId;

    @NotNull(message = "{EmailCheckOutRequest.books.notNull}")
    private List<BookCheckOutResponse> books;

}
