package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.UserDataAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.UserData;
import com.mycompany.myapp.repository.UserDataRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.UserDataDTO;
import com.mycompany.myapp.service.mapper.UserDataMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserDataResourceIT {

    private static final BigDecimal DEFAULT_SALARY = new BigDecimal(1);
    private static final BigDecimal UPDATED_SALARY = new BigDecimal(2);

    private static final Integer DEFAULT_FAMILY_LOAD = 1;
    private static final Integer UPDATED_FAMILY_LOAD = 2;

    private static final String DEFAULT_WORKPLACE = "AAAAAAAAAA";
    private static final String UPDATED_WORKPLACE = "BBBBBBBBBB";

    private static final String DEFAULT_HOUSING_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_HOUSING_TYPE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_RENT_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_RENT_COST = new BigDecimal(2);

    private static final Integer DEFAULT_YEARS_OF_EMPLOYMENT = 1;
    private static final Integer UPDATED_YEARS_OF_EMPLOYMENT = 2;

    private static final Integer DEFAULT_EMPLOYMENT_STATUS = 1;
    private static final Integer UPDATED_EMPLOYMENT_STATUS = 2;

    private static final String ENTITY_API_URL = "/api/user-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDataMapper userDataMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserDataMockMvc;

    private UserData userData;

    private UserData insertedUserData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createEntity() {
        return new UserData()
            .salary(DEFAULT_SALARY)
            .familyLoad(DEFAULT_FAMILY_LOAD)
            .workplace(DEFAULT_WORKPLACE)
            .housingType(DEFAULT_HOUSING_TYPE)
            .rentCost(DEFAULT_RENT_COST)
            .yearsOfEmployment(DEFAULT_YEARS_OF_EMPLOYMENT)
            .employmentStatus(DEFAULT_EMPLOYMENT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserData createUpdatedEntity() {
        return new UserData()
            .salary(UPDATED_SALARY)
            .familyLoad(UPDATED_FAMILY_LOAD)
            .workplace(UPDATED_WORKPLACE)
            .housingType(UPDATED_HOUSING_TYPE)
            .rentCost(UPDATED_RENT_COST)
            .yearsOfEmployment(UPDATED_YEARS_OF_EMPLOYMENT)
            .employmentStatus(UPDATED_EMPLOYMENT_STATUS);
    }

    @BeforeEach
    public void initTest() {
        userData = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserData != null) {
            userDataRepository.delete(insertedUserData);
            insertedUserData = null;
        }
    }

    @Test
    @Transactional
    void createUserData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);
        var returnedUserDataDTO = om.readValue(
            restUserDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserDataDTO.class
        );

        // Validate the UserData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserData = userDataMapper.toEntity(returnedUserDataDTO);
        assertUserDataUpdatableFieldsEquals(returnedUserData, getPersistedUserData(returnedUserData));

        insertedUserData = returnedUserData;
    }

    @Test
    @Transactional
    void createUserDataWithExistingId() throws Exception {
        // Create the UserData with an existing ID
        userData.setId(1L);
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSalaryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setSalary(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFamilyLoadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setFamilyLoad(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkWorkplaceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setWorkplace(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkYearsOfEmploymentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setYearsOfEmployment(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmploymentStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userData.setEmploymentStatus(null);

        // Create the UserData, which fails.
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        restUserDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        // Get all the userDataList
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userData.getId().intValue())))
            .andExpect(jsonPath("$.[*].salary").value(hasItem(sameNumber(DEFAULT_SALARY))))
            .andExpect(jsonPath("$.[*].familyLoad").value(hasItem(DEFAULT_FAMILY_LOAD)))
            .andExpect(jsonPath("$.[*].workplace").value(hasItem(DEFAULT_WORKPLACE)))
            .andExpect(jsonPath("$.[*].housingType").value(hasItem(DEFAULT_HOUSING_TYPE)))
            .andExpect(jsonPath("$.[*].rentCost").value(hasItem(sameNumber(DEFAULT_RENT_COST))))
            .andExpect(jsonPath("$.[*].yearsOfEmployment").value(hasItem(DEFAULT_YEARS_OF_EMPLOYMENT)))
            .andExpect(jsonPath("$.[*].employmentStatus").value(hasItem(DEFAULT_EMPLOYMENT_STATUS)));
    }

    @Test
    @Transactional
    void getUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        // Get the userData
        restUserDataMockMvc
            .perform(get(ENTITY_API_URL_ID, userData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userData.getId().intValue()))
            .andExpect(jsonPath("$.salary").value(sameNumber(DEFAULT_SALARY)))
            .andExpect(jsonPath("$.familyLoad").value(DEFAULT_FAMILY_LOAD))
            .andExpect(jsonPath("$.workplace").value(DEFAULT_WORKPLACE))
            .andExpect(jsonPath("$.housingType").value(DEFAULT_HOUSING_TYPE))
            .andExpect(jsonPath("$.rentCost").value(sameNumber(DEFAULT_RENT_COST)))
            .andExpect(jsonPath("$.yearsOfEmployment").value(DEFAULT_YEARS_OF_EMPLOYMENT))
            .andExpect(jsonPath("$.employmentStatus").value(DEFAULT_EMPLOYMENT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingUserData() throws Exception {
        // Get the userData
        restUserDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData
        UserData updatedUserData = userDataRepository.findById(userData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserData are not directly saved in db
        em.detach(updatedUserData);
        updatedUserData
            .salary(UPDATED_SALARY)
            .familyLoad(UPDATED_FAMILY_LOAD)
            .workplace(UPDATED_WORKPLACE)
            .housingType(UPDATED_HOUSING_TYPE)
            .rentCost(UPDATED_RENT_COST)
            .yearsOfEmployment(UPDATED_YEARS_OF_EMPLOYMENT)
            .employmentStatus(UPDATED_EMPLOYMENT_STATUS);
        UserDataDTO userDataDTO = userDataMapper.toDto(updatedUserData);

        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserDataToMatchAllProperties(updatedUserData);
    }

    @Test
    @Transactional
    void putNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData
            .workplace(UPDATED_WORKPLACE)
            .housingType(UPDATED_HOUSING_TYPE)
            .rentCost(UPDATED_RENT_COST)
            .yearsOfEmployment(UPDATED_YEARS_OF_EMPLOYMENT)
            .employmentStatus(UPDATED_EMPLOYMENT_STATUS);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserData, userData), getPersistedUserData(userData));
    }

    @Test
    @Transactional
    void fullUpdateUserDataWithPatch() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userData using partial update
        UserData partialUpdatedUserData = new UserData();
        partialUpdatedUserData.setId(userData.getId());

        partialUpdatedUserData
            .salary(UPDATED_SALARY)
            .familyLoad(UPDATED_FAMILY_LOAD)
            .workplace(UPDATED_WORKPLACE)
            .housingType(UPDATED_HOUSING_TYPE)
            .rentCost(UPDATED_RENT_COST)
            .yearsOfEmployment(UPDATED_YEARS_OF_EMPLOYMENT)
            .employmentStatus(UPDATED_EMPLOYMENT_STATUS);

        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserData))
            )
            .andExpect(status().isOk());

        // Validate the UserData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserDataUpdatableFieldsEquals(partialUpdatedUserData, getPersistedUserData(partialUpdatedUserData));
    }

    @Test
    @Transactional
    void patchNonExistingUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userDataDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userDataDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userData.setId(longCount.incrementAndGet());

        // Create the UserData
        UserDataDTO userDataDTO = userDataMapper.toDto(userData);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userDataDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserData() throws Exception {
        // Initialize the database
        insertedUserData = userDataRepository.saveAndFlush(userData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userData
        restUserDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, userData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userDataRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserData getPersistedUserData(UserData userData) {
        return userDataRepository.findById(userData.getId()).orElseThrow();
    }

    protected void assertPersistedUserDataToMatchAllProperties(UserData expectedUserData) {
        assertUserDataAllPropertiesEquals(expectedUserData, getPersistedUserData(expectedUserData));
    }

    protected void assertPersistedUserDataToMatchUpdatableProperties(UserData expectedUserData) {
        assertUserDataAllUpdatablePropertiesEquals(expectedUserData, getPersistedUserData(expectedUserData));
    }
}
