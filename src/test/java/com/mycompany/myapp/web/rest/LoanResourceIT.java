package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.LoanAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Loan;
import com.mycompany.myapp.repository.LoanRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.LoanDTO;
import com.mycompany.myapp.service.mapper.LoanMapper;
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
 * Integration tests for the {@link LoanResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LoanResourceIT {

    private static final BigDecimal DEFAULT_REQUESTED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_REQUESTED_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_INTEREST_RATE = new BigDecimal(2);

    private static final Integer DEFAULT_PAYMENT_TERM_MONTHS = 1;
    private static final Integer UPDATED_PAYMENT_TERM_MONTHS = 2;

    private static final LocalDate DEFAULT_APPLICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_APPLICATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final String ENTITY_API_URL = "/api/loans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanMapper loanMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLoanMockMvc;

    private Loan loan;

    private Loan insertedLoan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createEntity() {
        return new Loan()
            .requestedAmount(DEFAULT_REQUESTED_AMOUNT)
            .interestRate(DEFAULT_INTEREST_RATE)
            .paymentTermMonths(DEFAULT_PAYMENT_TERM_MONTHS)
            .applicationDate(DEFAULT_APPLICATION_DATE)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Loan createUpdatedEntity() {
        return new Loan()
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .interestRate(UPDATED_INTEREST_RATE)
            .paymentTermMonths(UPDATED_PAYMENT_TERM_MONTHS)
            .applicationDate(UPDATED_APPLICATION_DATE)
            .status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        loan = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedLoan != null) {
            loanRepository.delete(insertedLoan);
            insertedLoan = null;
        }
    }

    @Test
    @Transactional
    void createLoan() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);
        var returnedLoanDTO = om.readValue(
            restLoanMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LoanDTO.class
        );

        // Validate the Loan in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLoan = loanMapper.toEntity(returnedLoanDTO);
        assertLoanUpdatableFieldsEquals(returnedLoan, getPersistedLoan(returnedLoan));

        insertedLoan = returnedLoan;
    }

    @Test
    @Transactional
    void createLoanWithExistingId() throws Exception {
        // Create the Loan with an existing ID
        loan.setId(1L);
        LoanDTO loanDTO = loanMapper.toDto(loan);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRequestedAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setRequestedAmount(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInterestRateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setInterestRate(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentTermMonthsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setPaymentTermMonths(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        loan.setStatus(null);

        // Create the Loan, which fails.
        LoanDTO loanDTO = loanMapper.toDto(loan);

        restLoanMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLoans() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get all the loanList
        restLoanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(loan.getId().intValue())))
            .andExpect(jsonPath("$.[*].requestedAmount").value(hasItem(sameNumber(DEFAULT_REQUESTED_AMOUNT))))
            .andExpect(jsonPath("$.[*].interestRate").value(hasItem(sameNumber(DEFAULT_INTEREST_RATE))))
            .andExpect(jsonPath("$.[*].paymentTermMonths").value(hasItem(DEFAULT_PAYMENT_TERM_MONTHS)))
            .andExpect(jsonPath("$.[*].applicationDate").value(hasItem(DEFAULT_APPLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        // Get the loan
        restLoanMockMvc
            .perform(get(ENTITY_API_URL_ID, loan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(loan.getId().intValue()))
            .andExpect(jsonPath("$.requestedAmount").value(sameNumber(DEFAULT_REQUESTED_AMOUNT)))
            .andExpect(jsonPath("$.interestRate").value(sameNumber(DEFAULT_INTEREST_RATE)))
            .andExpect(jsonPath("$.paymentTermMonths").value(DEFAULT_PAYMENT_TERM_MONTHS))
            .andExpect(jsonPath("$.applicationDate").value(DEFAULT_APPLICATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingLoan() throws Exception {
        // Get the loan
        restLoanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan
        Loan updatedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLoan are not directly saved in db
        em.detach(updatedLoan);
        updatedLoan
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .interestRate(UPDATED_INTEREST_RATE)
            .paymentTermMonths(UPDATED_PAYMENT_TERM_MONTHS)
            .applicationDate(UPDATED_APPLICATION_DATE)
            .status(UPDATED_STATUS);
        LoanDTO loanDTO = loanMapper.toDto(updatedLoan);

        restLoanMockMvc
            .perform(put(ENTITY_API_URL_ID, loanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isOk());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLoanToMatchAllProperties(updatedLoan);
    }

    @Test
    @Transactional
    void putNonExistingLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(put(ENTITY_API_URL_ID, loanDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLoanWithPatch() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan using partial update
        Loan partialUpdatedLoan = new Loan();
        partialUpdatedLoan.setId(loan.getId());

        partialUpdatedLoan
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .interestRate(UPDATED_INTEREST_RATE)
            .applicationDate(UPDATED_APPLICATION_DATE)
            .status(UPDATED_STATUS);

        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoan))
            )
            .andExpect(status().isOk());

        // Validate the Loan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoanUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLoan, loan), getPersistedLoan(loan));
    }

    @Test
    @Transactional
    void fullUpdateLoanWithPatch() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the loan using partial update
        Loan partialUpdatedLoan = new Loan();
        partialUpdatedLoan.setId(loan.getId());

        partialUpdatedLoan
            .requestedAmount(UPDATED_REQUESTED_AMOUNT)
            .interestRate(UPDATED_INTEREST_RATE)
            .paymentTermMonths(UPDATED_PAYMENT_TERM_MONTHS)
            .applicationDate(UPDATED_APPLICATION_DATE)
            .status(UPDATED_STATUS);

        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLoan.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLoan))
            )
            .andExpect(status().isOk());

        // Validate the Loan in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLoanUpdatableFieldsEquals(partialUpdatedLoan, getPersistedLoan(partialUpdatedLoan));
    }

    @Test
    @Transactional
    void patchNonExistingLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, loanDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(loanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLoan() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        loan.setId(longCount.incrementAndGet());

        // Create the Loan
        LoanDTO loanDTO = loanMapper.toDto(loan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLoanMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(loanDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Loan in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLoan() throws Exception {
        // Initialize the database
        insertedLoan = loanRepository.saveAndFlush(loan);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the loan
        restLoanMockMvc
            .perform(delete(ENTITY_API_URL_ID, loan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return loanRepository.count();
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

    protected Loan getPersistedLoan(Loan loan) {
        return loanRepository.findById(loan.getId()).orElseThrow();
    }

    protected void assertPersistedLoanToMatchAllProperties(Loan expectedLoan) {
        assertLoanAllPropertiesEquals(expectedLoan, getPersistedLoan(expectedLoan));
    }

    protected void assertPersistedLoanToMatchUpdatableProperties(Loan expectedLoan) {
        assertLoanAllUpdatablePropertiesEquals(expectedLoan, getPersistedLoan(expectedLoan));
    }
}
