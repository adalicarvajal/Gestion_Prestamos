package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AmortizationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Amortization;
import com.mycompany.myapp.repository.AmortizationRepository;
import com.mycompany.myapp.service.dto.AmortizationDTO;
import com.mycompany.myapp.service.mapper.AmortizationMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link AmortizationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AmortizationResourceIT {

    private static final Integer DEFAULT_INSTALLMENT_NUMBER = 1;
    private static final Integer UPDATED_INSTALLMENT_NUMBER = 2;

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_REMAINING_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_REMAINING_BALANCE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PRINCIPAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRINCIPAL = new BigDecimal(2);

    private static final LocalDate DEFAULT_PAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final BigDecimal DEFAULT_PAYMENT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAYMENT_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PENALTY_INTEREST = new BigDecimal(1);
    private static final BigDecimal UPDATED_PENALTY_INTEREST = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/amortizations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AmortizationRepository amortizationRepository;

    @Autowired
    private AmortizationMapper amortizationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAmortizationMockMvc;

    private Amortization amortization;

    private Amortization insertedAmortization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Amortization createEntity() {
        return new Amortization()
            .installmentNumber(DEFAULT_INSTALLMENT_NUMBER)
            .dueDate(DEFAULT_DUE_DATE)
            .remainingBalance(DEFAULT_REMAINING_BALANCE)
            .principal(DEFAULT_PRINCIPAL)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT)
            .penaltyInterest(DEFAULT_PENALTY_INTEREST);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Amortization createUpdatedEntity() {
        return new Amortization()
            .installmentNumber(UPDATED_INSTALLMENT_NUMBER)
            .dueDate(UPDATED_DUE_DATE)
            .remainingBalance(UPDATED_REMAINING_BALANCE)
            .principal(UPDATED_PRINCIPAL)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .penaltyInterest(UPDATED_PENALTY_INTEREST);
    }

    @BeforeEach
    public void initTest() {
        amortization = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAmortization != null) {
            amortizationRepository.delete(insertedAmortization);
            insertedAmortization = null;
        }
    }

    @Test
    @Transactional
    void createAmortization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);
        var returnedAmortizationDTO = om.readValue(
            restAmortizationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AmortizationDTO.class
        );

        // Validate the Amortization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAmortization = amortizationMapper.toEntity(returnedAmortizationDTO);
        assertAmortizationUpdatableFieldsEquals(returnedAmortization, getPersistedAmortization(returnedAmortization));

        insertedAmortization = returnedAmortization;
    }

    @Test
    @Transactional
    void createAmortizationWithExistingId() throws Exception {
        // Create the Amortization with an existing ID
        amortization.setId(1L);
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkInstallmentNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amortization.setInstallmentNumber(null);

        // Create the Amortization, which fails.
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amortization.setDueDate(null);

        // Create the Amortization, which fails.
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRemainingBalanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amortization.setRemainingBalance(null);

        // Create the Amortization, which fails.
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrincipalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amortization.setPrincipal(null);

        // Create the Amortization, which fails.
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        amortization.setPaymentAmount(null);

        // Create the Amortization, which fails.
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        restAmortizationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAmortizations() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        // Get all the amortizationList
        restAmortizationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(amortization.getId().intValue())))
            .andExpect(jsonPath("$.[*].installmentNumber").value(hasItem(DEFAULT_INSTALLMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].remainingBalance").value(hasItem(sameNumber(DEFAULT_REMAINING_BALANCE))))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(sameNumber(DEFAULT_PRINCIPAL))))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(sameNumber(DEFAULT_PAYMENT_AMOUNT))))
            .andExpect(jsonPath("$.[*].penaltyInterest").value(hasItem(sameNumber(DEFAULT_PENALTY_INTEREST))));
    }

    @Test
    @Transactional
    void getAmortization() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        // Get the amortization
        restAmortizationMockMvc
            .perform(get(ENTITY_API_URL_ID, amortization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(amortization.getId().intValue()))
            .andExpect(jsonPath("$.installmentNumber").value(DEFAULT_INSTALLMENT_NUMBER))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.remainingBalance").value(sameNumber(DEFAULT_REMAINING_BALANCE)))
            .andExpect(jsonPath("$.principal").value(sameNumber(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.paymentAmount").value(sameNumber(DEFAULT_PAYMENT_AMOUNT)))
            .andExpect(jsonPath("$.penaltyInterest").value(sameNumber(DEFAULT_PENALTY_INTEREST)));
    }

    @Test
    @Transactional
    void getNonExistingAmortization() throws Exception {
        // Get the amortization
        restAmortizationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAmortization() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amortization
        Amortization updatedAmortization = amortizationRepository.findById(amortization.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAmortization are not directly saved in db
        em.detach(updatedAmortization);
        updatedAmortization
            .installmentNumber(UPDATED_INSTALLMENT_NUMBER)
            .dueDate(UPDATED_DUE_DATE)
            .remainingBalance(UPDATED_REMAINING_BALANCE)
            .principal(UPDATED_PRINCIPAL)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .penaltyInterest(UPDATED_PENALTY_INTEREST);
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(updatedAmortization);

        restAmortizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amortizationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amortizationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAmortizationToMatchAllProperties(updatedAmortization);
    }

    @Test
    @Transactional
    void putNonExistingAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, amortizationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amortizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(amortizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAmortizationWithPatch() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amortization using partial update
        Amortization partialUpdatedAmortization = new Amortization();
        partialUpdatedAmortization.setId(amortization.getId());

        partialUpdatedAmortization
            .dueDate(UPDATED_DUE_DATE)
            .remainingBalance(UPDATED_REMAINING_BALANCE)
            .principal(UPDATED_PRINCIPAL)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .penaltyInterest(UPDATED_PENALTY_INTEREST);

        restAmortizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmortization.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmortization))
            )
            .andExpect(status().isOk());

        // Validate the Amortization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmortizationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAmortization, amortization),
            getPersistedAmortization(amortization)
        );
    }

    @Test
    @Transactional
    void fullUpdateAmortizationWithPatch() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the amortization using partial update
        Amortization partialUpdatedAmortization = new Amortization();
        partialUpdatedAmortization.setId(amortization.getId());

        partialUpdatedAmortization
            .installmentNumber(UPDATED_INSTALLMENT_NUMBER)
            .dueDate(UPDATED_DUE_DATE)
            .remainingBalance(UPDATED_REMAINING_BALANCE)
            .principal(UPDATED_PRINCIPAL)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .penaltyInterest(UPDATED_PENALTY_INTEREST);

        restAmortizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAmortization.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAmortization))
            )
            .andExpect(status().isOk());

        // Validate the Amortization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAmortizationUpdatableFieldsEquals(partialUpdatedAmortization, getPersistedAmortization(partialUpdatedAmortization));
    }

    @Test
    @Transactional
    void patchNonExistingAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, amortizationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amortizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(amortizationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAmortization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        amortization.setId(longCount.incrementAndGet());

        // Create the Amortization
        AmortizationDTO amortizationDTO = amortizationMapper.toDto(amortization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAmortizationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(amortizationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Amortization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAmortization() throws Exception {
        // Initialize the database
        insertedAmortization = amortizationRepository.saveAndFlush(amortization);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the amortization
        restAmortizationMockMvc
            .perform(delete(ENTITY_API_URL_ID, amortization.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return amortizationRepository.count();
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

    protected Amortization getPersistedAmortization(Amortization amortization) {
        return amortizationRepository.findById(amortization.getId()).orElseThrow();
    }

    protected void assertPersistedAmortizationToMatchAllProperties(Amortization expectedAmortization) {
        assertAmortizationAllPropertiesEquals(expectedAmortization, getPersistedAmortization(expectedAmortization));
    }

    protected void assertPersistedAmortizationToMatchUpdatableProperties(Amortization expectedAmortization) {
        assertAmortizationAllUpdatablePropertiesEquals(expectedAmortization, getPersistedAmortization(expectedAmortization));
    }
}
