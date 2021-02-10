package capstone.library.dtos.request;

import capstone.library.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatronRequest {

    @Email(message = "{account.email.invalid}")
    @NotNull(message = "{account.email.notNull}")
    private String email;

    private String rfid;

    private String avatar;

    private int creatorId;

    private int patronTypeId;

    private String fullName;

    private String phone;

    private Gender gender;
}
