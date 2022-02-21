package capstone.library.services.impl;

import capstone.library.dtos.response.BorrowingResDto;
import capstone.library.entities.Borrowing;
import capstone.library.mappers.BorrowingMapper;
import capstone.library.repositories.BorrowingRepository;
import capstone.library.services.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BorrowingServiceImpl implements BorrowingService {
    @Autowired
    BorrowingRepository borrowingRepository;
    @Autowired
    BorrowingMapper borrowingMapper;

    @Override
    public Optional<Borrowing> findById(int id) {
        return borrowingRepository.findById(id);
    }

    @Override
    public Page<BorrowingResDto> findAll(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        if (from != null && to != null) {
            if (from.isAfter(to)) {
                LocalDateTime tmp = from;
                from = to;
                to = tmp;
            }

            Page<Borrowing> page = borrowingRepository.findAllByBorrowedAtBetween(from, to, pageable);
            return new PageImpl<BorrowingResDto>(page.map(borrowingResDto -> borrowingMapper.toDto(borrowingResDto)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());

        } else {
            Page<Borrowing> page = borrowingRepository.findAll(pageable);
            return new PageImpl<BorrowingResDto>(page.map(borrowingResDto -> borrowingMapper.toDto(borrowingResDto)).stream().collect(Collectors.toList()), pageable, page.getTotalElements());
        }

    }
}
