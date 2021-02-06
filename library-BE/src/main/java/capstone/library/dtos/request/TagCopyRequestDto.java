package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TagCopyRequestDto implements Serializable
{
    @NotEmpty(message = "{TagCopyRequestDto.barcode.notEmpty}")
    private String barcode;
    @NotEmpty(message = "{TagCopyRequestDto.rfid.notEmpty}")
    private String rfid;
    @NotNull(message = "{TagCopyRequestDto.updater.notNull}")
    private int updater;
}
