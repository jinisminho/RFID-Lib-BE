package capstone.library.services.impl;

import capstone.library.dtos.request.CreateBorrowPolicyRequest;
import capstone.library.dtos.request.UpdateBorrowPolicyRequest;
import capstone.library.dtos.response.BorrowPolicyResponse;
import capstone.library.entities.BookCopyType;
import capstone.library.entities.BorrowPolicy;
import capstone.library.entities.PatronType;
import capstone.library.exceptions.CustomException;
import capstone.library.exceptions.MissingInputException;
import capstone.library.exceptions.ResourceNotFoundException;
import capstone.library.repositories.BookCopyTypeRepository;
import capstone.library.repositories.BorrowPolicyRepository;
import capstone.library.repositories.PatronTypeRepository;
import capstone.library.services.BorrowPolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static capstone.library.util.constants.ConstantUtil.DELETE_SUCCESS;

@Service
public class BorrowPolicyServiceImpl implements BorrowPolicyService {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    BorrowPolicyRepository borrowPolicyRepo;

    @Autowired
    PatronTypeRepository patronTypeRepo;

    @Autowired
    BookCopyTypeRepository bookCopyTypeRepo;

    @Override
    public Page<BorrowPolicyResponse> getBorrowPolicies(Pageable pageable,
                                                        Integer patronTypeId,
                                                        Integer bookCopyTypeId) {
        Page<BorrowPolicy> rs;
        if(patronTypeId == null && bookCopyTypeId == null){
           rs = borrowPolicyRepo.findAll(pageable);
        }else if (patronTypeId != null && bookCopyTypeId == null){
            rs = borrowPolicyRepo.findByPatronTypeId(pageable, patronTypeId);
        }else if(patronTypeId == null){
            rs = borrowPolicyRepo.findByBookCopyTypeId(pageable, bookCopyTypeId);
        }else{
            rs = borrowPolicyRepo.findByPatronTypeIdAndBookCopyTypeId(pageable, patronTypeId, bookCopyTypeId);
        }
        return rs.map(b -> mapper.convertValue(b, BorrowPolicyResponse.class));
    }

    @Override
    public BorrowPolicyResponse addBorrowPolicy(CreateBorrowPolicyRequest request) {
        if(request == null){
            throw new MissingInputException("missing borrow policy request");
        }
        PatronType patronType = patronTypeRepo.findById(request.getPatronTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type",
                        "Cannot find patron type id " + request.getPatronTypeId()));
        BookCopyType copyType = bookCopyTypeRepo.findById(request.getBookCopyTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron Type",
                        "Cannot find copy type id " + request.getBookCopyTypeId()));
        Optional<BorrowPolicy> tmp = borrowPolicyRepo.findByPatronTypeIdAndBookCopyTypeId(request.getPatronTypeId(), request.getBookCopyTypeId());
        if(tmp.isPresent()){
            throw new CustomException(HttpStatus.BAD_REQUEST, "conflict resource",
                    "Policy for patron type: " + patronType.getName() +
                    " book copy type: " + copyType.getName() + " has been set already.");
        }
        BorrowPolicy borrowPolicy = mapper.convertValue(request, BorrowPolicy.class);
        borrowPolicy.setPatronType(patronType);
        borrowPolicy.setBookCopyType(copyType);
        BorrowPolicy newBorrowPolicy = borrowPolicyRepo.save(borrowPolicy);
        return mapper.convertValue(newBorrowPolicy,BorrowPolicyResponse.class);
    }

    @Override
    public BorrowPolicyResponse updateBorrowPolicy(UpdateBorrowPolicyRequest request) {
        if(request == null){
            throw new MissingInputException("missing borrow policy request");
        }
        BorrowPolicy borrowPolicy = findBorrowPolicyById(request.getId());
        borrowPolicy.setDueDuration(request.getDueDuration());
        borrowPolicy.setMaxNumberCopyBorrow(request.getMaxNumberCopyBorrow());
        borrowPolicy.setMaxExtendTime(request.getMaxExtendTime());
        borrowPolicy.setExtendDueDuration(request.getExtendDueDuration());
        BorrowPolicy updatedPolicy  =  borrowPolicyRepo.save(borrowPolicy);
        return mapper.convertValue(updatedPolicy, BorrowPolicyResponse.class);
    }

    @Override
    public String deleteBorrowPolicy(int id) {
        BorrowPolicy borrowPolicy = findBorrowPolicyById(id);
        borrowPolicyRepo.delete(borrowPolicy);
        return DELETE_SUCCESS;
    }

    private BorrowPolicy findBorrowPolicyById(int id){
        return borrowPolicyRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow policy",
                        "Cannot find borrow policy with id: " + id));
    }
}
