package capstone.library.services.impl;

import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.dtos.request.UpdatePatronTypePolicyRequest;
import capstone.library.entities.PatronType;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.PatronTypeRepository;
import capstone.library.services.PatronTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static capstone.library.util.ConstantUtil.UPDATE_SUCCESS;


@Service
public class PatronTypeServiceImpl implements PatronTypeService {

    @Autowired
    PatronTypeRepository patronTypeRepo;

    @Autowired
    ObjectMapper mapper;

    @Override
    public List<PatronTypeDto> getAllPatronType() {
        return patronTypeRepo
                .findAll()
                .stream()
                .map(p -> mapper.convertValue(p, PatronTypeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<PatronTypeDto> getPatronType(Pageable pageable, String name) {
        Page<PatronType> rs;
        if(name != null){
            rs= patronTypeRepo.findByNameContains(pageable, name);
        }else{
            rs = patronTypeRepo.findAll(pageable);
        }

        return rs.map(p -> mapper.convertValue(p, PatronTypeDto.class));
    }

    @Override
    public String updatePatronTypePolicy(UpdatePatronTypePolicyRequest request) {
        PatronType patronType = findPatronTypeById(request.getId());
        patronType.setMaxBorrowNumber(request.getMaxBorrowNumber());
        patronTypeRepo.save(patronType);
        return UPDATE_SUCCESS;
    }

    private PatronType findPatronTypeById(int id){
        return patronTypeRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type"
                ,"Cannot find patron type with id: " + id));
    }
}
