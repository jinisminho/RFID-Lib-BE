package capstone.library.services.impl;

import capstone.library.dtos.common.PatronTypeDto;
import capstone.library.repositories.PatronTypeRepository;
import capstone.library.services.PatronTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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
}
