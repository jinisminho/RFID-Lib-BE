package capstone.library.dtos.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SaveSamplePositionRequestDto {
    List<String> rfids;
    int updater;
    int positionId;
}
