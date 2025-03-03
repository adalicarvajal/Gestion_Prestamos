package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.repository.UserDataRepository;
import com.mycompany.myapp.service.UserDataService;
import com.mycompany.myapp.service.dto.UserDataDTO;
import com.mycompany.myapp.service.mapper.UserDataMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.UserData}.
 */
@Service
@Transactional
public class UserDataServiceImpl implements UserDataService {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataServiceImpl.class);

    private final UserDataRepository userDataRepository;

    private final UserDataMapper userDataMapper;

    public UserDataServiceImpl(UserDataRepository userDataRepository, UserDataMapper userDataMapper) {
        this.userDataRepository = userDataRepository;
        this.userDataMapper = userDataMapper;
    }

    @Override
    public UserDataDTO save(UserDataDTO userDataDTO) {
        LOG.debug("Request to save UserData : {}", userDataDTO);
        UserData userData = userDataMapper.toEntity(userDataDTO);
        userData = userDataRepository.save(userData);
        return userDataMapper.toDto(userData);
    }

    @Override
    public UserDataDTO update(UserDataDTO userDataDTO) {
        LOG.debug("Request to update UserData : {}", userDataDTO);
        UserData userData = userDataMapper.toEntity(userDataDTO);
        userData = userDataRepository.save(userData);
        return userDataMapper.toDto(userData);
    }

    @Override
    public Optional<UserDataDTO> partialUpdate(UserDataDTO userDataDTO) {
        LOG.debug("Request to partially update UserData : {}", userDataDTO);

        return userDataRepository
            .findById(userDataDTO.getId())
            .map(existingUserData -> {
                userDataMapper.partialUpdate(existingUserData, userDataDTO);

                return existingUserData;
            })
            .map(userDataRepository::save)
            .map(userDataMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDataDTO> findAll() {
        LOG.debug("Request to get all UserData");
        return userDataRepository.findAll().stream().map(userDataMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDataDTO> findOne(Long id) {
        LOG.debug("Request to get UserData : {}", id);
        return userDataRepository.findById(id).map(userDataMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete UserData : {}", id);
        userDataRepository.deleteById(id);
    }
}
