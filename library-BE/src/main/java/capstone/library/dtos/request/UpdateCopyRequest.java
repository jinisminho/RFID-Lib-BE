package capstone.library.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCopyRequest implements Serializable
{
    @NotNull(message = "{UpdateCopyRequest.id.notNull}")
    private int id;
    @NotEmpty(message = "{UpdateCopyRequest.rfid.notEmpty}")
    private String rfid;
    @NotNull(message = "{UpdateCopyRequest.price.notNull}")
    private double price;
    @NotNull(message = "{UpdateCopyRequest.copyType.notNull}")
    private int copyTypeId;
    @NotNull(message = "{UpdateCopyRequest.updater.notNull}")
    private int updater;
}
