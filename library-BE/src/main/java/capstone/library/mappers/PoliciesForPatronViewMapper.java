package capstone.library.mappers;

import capstone.library.dtos.response.policiesForPatronView.BorrowPolicyForPatronViewResDto;
import capstone.library.dtos.response.policiesForPatronView.FeePolicyForPatronViewResDto;
import capstone.library.dtos.response.policiesForPatronView.PatronPolicyForPatronViewResDto;
import capstone.library.entities.BorrowPolicy;
import capstone.library.entities.FeePolicy;
import capstone.library.entities.PatronType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PoliciesForPatronViewMapper {

    @Mappings({
            @Mapping(target = "maxBorrowNumber", source = "maxNumberCopyBorrow"),
    })
    BorrowPolicyForPatronViewResDto toBorrowDto(BorrowPolicy entity);

    @Mappings({
            @Mapping(target = "maxPercentageOverdue", source = "maxPercentageOverdueFine"),
            @Mapping(target = "documentProcessingFee", source = "documentProcessing_Fee"),
    })
    FeePolicyForPatronViewResDto toFeeDto(FeePolicy entity);

    PatronPolicyForPatronViewResDto toPatronDto(PatronType entity);

}
