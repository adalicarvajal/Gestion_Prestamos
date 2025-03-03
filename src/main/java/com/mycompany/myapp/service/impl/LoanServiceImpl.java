package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Loan;
import com.mycompany.myapp.repository.LoanRepository;
import com.mycompany.myapp.service.LoanService;
import com.mycompany.myapp.service.dto.LoanDTO;
import com.mycompany.myapp.service.mapper.LoanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Loan}.
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanRepository loanRepository;

    private final LoanMapper loanMapper;

    public LoanServiceImpl(LoanRepository loanRepository, LoanMapper loanMapper) {
        this.loanRepository = loanRepository;
        this.loanMapper = loanMapper;
    }

    @Override
    public LoanDTO save(LoanDTO loanDTO) {
        LOG.debug("Request to save Loan : {}", loanDTO);
        Loan loan = loanMapper.toEntity(loanDTO);
        loan = loanRepository.save(loan);
        return loanMapper.toDto(loan);
    }

    @Override
    public LoanDTO update(LoanDTO loanDTO) {
        LOG.debug("Request to update Loan : {}", loanDTO);
        Loan loan = loanMapper.toEntity(loanDTO);
        loan = loanRepository.save(loan);
        return loanMapper.toDto(loan);
    }

    @Override
    public Optional<LoanDTO> partialUpdate(LoanDTO loanDTO) {
        LOG.debug("Request to partially update Loan : {}", loanDTO);

        return loanRepository
            .findById(loanDTO.getId())
            .map(existingLoan -> {
                loanMapper.partialUpdate(existingLoan, loanDTO);

                return existingLoan;
            })
            .map(loanRepository::save)
            .map(loanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Loans");
        return loanRepository.findAll(pageable).map(loanMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoanDTO> findOne(Long id) {
        LOG.debug("Request to get Loan : {}", id);
        return loanRepository.findById(id).map(loanMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Loan : {}", id);
        loanRepository.deleteById(id);
    }
}
