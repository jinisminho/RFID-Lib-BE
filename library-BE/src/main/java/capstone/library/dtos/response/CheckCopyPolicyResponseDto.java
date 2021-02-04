package capstone.library.dtos.response;

import capstone.library.dtos.common.MyBookDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckCopyPolicyResponseDto
{
    private MyBookDto copy;

    private boolean violatePolicy;

    private List<String> reasons;
}
