package capstone.library.services;

import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.dtos.request.AddPatronTypeReqDto;
import capstone.library.dtos.request.PatronTypeReqDto;
import capstone.library.dtos.request.UpdatePatronTypePolicyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatronTypeService {

    List<PatronTypeDto> getAllPatronType();

    Page<PatronTypeDto> getPatronType(Pageable pageable, String name);

    String updatePatronTypePolicy(UpdatePatronTypePolicyRequest request);

    boolean addPatronType(AddPatronTypeReqDto req);

    boolean updatePatronType(Integer id, PatronTypeReqDto req);

    boolean deletePatronType(Integer id);

}
