package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Amortization;
import com.mycompany.myapp.repository.AmortizationRepository;
import com.mycompany.myapp.service.AmortizationService;
import com.mycompany.myapp.service.dto.AmortizationDTO;
import com.mycompany.myapp.service.mapper.AmortizationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Amortization}.
 */
@Service
@Transactional
public class AmortizationServiceImpl implements AmortizationService {

    private static final Logger LOG = LoggerFactory.getLogger(AmortizationServiceImpl.class);

    private final AmortizationRepository amortizationRepository;

    private final AmortizationMapper amortizationMapper;

    public AmortizationServiceImpl(AmortizationRepository amortizationRepository, AmortizationMapper amortizationMapper) {
        this.amortizationRepository = amortizationRepository;
        this.amortizationMapper = amortizationMapper;
    }

    @Override
    public Page<AmortizationDTO> findAllByUserId(Long userId, Pageable pageable) {
        return amortizationRepository.findByLoanUserId(userId, pageable)
            .map(amortizationMapper::toDto);
    }

    @Override
    public Page<AmortizationDTO> findAllByLoanId(Long loanId, Pageable pageable) {
        LOG.debug("Request to get all Amortizations by loanId");
        return amortizationRepository.findByLoanId(loanId, pageable).map(amortizationMapper::toDto);
    }

    public Page<AmortizationDTO> findAllByCurrentUser(Pageable pageable) {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return amortizationRepository.findAllByLoan_User_Login(currentUser, pageable).map(amortizationMapper::toDto);
    }


    @Override
    public AmortizationDTO save(AmortizationDTO amortizationDTO) {
        LOG.debug("Request to save Amortization : {}", amortizationDTO);
        Amortization amortization = amortizationMapper.toEntity(amortizationDTO);
        amortization = amortizationRepository.save(amortization);
        return amortizationMapper.toDto(amortization);
    }

    @Override
    public AmortizationDTO update(AmortizationDTO amortizationDTO) {
        LOG.debug("Request to update Amortization : {}", amortizationDTO);
        Amortization amortization = amortizationMapper.toEntity(amortizationDTO);
        amortization = amortizationRepository.save(amortization);
        return amortizationMapper.toDto(amortization);
    }

    @Override
    public Optional<AmortizationDTO> partialUpdate(AmortizationDTO amortizationDTO) {
        LOG.debug("Request to partially update Amortization : {}", amortizationDTO);

        return amortizationRepository
            .findById(amortizationDTO.getId())
            .map(existingAmortization -> {
                amortizationMapper.partialUpdate(existingAmortization, amortizationDTO);

                return existingAmortization;
            })
            .map(amortizationRepository::save)
            .map(amortizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AmortizationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Amortizations");
        return amortizationRepository.findAll(pageable).map(amortizationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AmortizationDTO> findOne(Long id) {
        LOG.debug("Request to get Amortization : {}", id);
        return amortizationRepository.findById(id).map(amortizationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Amortization : {}", id);
        amortizationRepository.deleteById(id);
    }
}
