package capstone.library.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateBarcodesResponseDto
{
    BookResponseDto bookInfo;
    List<String> generatedBarcodes;
}
