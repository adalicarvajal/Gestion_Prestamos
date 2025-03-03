import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Loan e2e test', () => {
  const loanPageUrl = '/loan';
  const loanPageUrlPattern = new RegExp('/loan(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const loanSample = { requestedAmount: 24142.01, interestRate: 6776.27, paymentTermMonths: 12407, status: 12615 };

  let loan;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/loans+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/loans').as('postEntityRequest');
    cy.intercept('DELETE', '/api/loans/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (loan) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/loans/${loan.id}`,
      }).then(() => {
        loan = undefined;
      });
    }
  });

  it('Loans menu should load Loans page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('loan');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Loan').should('exist');
    cy.url().should('match', loanPageUrlPattern);
  });

  describe('Loan page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(loanPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Loan page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/loan/new$'));
        cy.getEntityCreateUpdateHeading('Loan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', loanPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/loans',
          body: loanSample,
        }).then(({ body }) => {
          loan = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/loans+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/loans?page=0&size=20>; rel="last",<http://localhost/api/loans?page=0&size=20>; rel="first"',
              },
              body: [loan],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(loanPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Loan page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('loan');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', loanPageUrlPattern);
      });

      it('edit button click should load edit Loan page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Loan');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', loanPageUrlPattern);
      });

      it('edit button click should load edit Loan page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Loan');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', loanPageUrlPattern);
      });

      it('last delete button click should delete instance of Loan', () => {
        cy.intercept('GET', '/api/loans/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('loan').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', loanPageUrlPattern);

        loan = undefined;
      });
    });
  });

  describe('new Loan page', () => {
    beforeEach(() => {
      cy.visit(`${loanPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Loan');
    });

    it('should create an instance of Loan', () => {
      cy.get(`[data-cy="requestedAmount"]`).type('31664.02');
      cy.get(`[data-cy="requestedAmount"]`).should('have.value', '31664.02');

      cy.get(`[data-cy="interestRate"]`).type('15037.59');
      cy.get(`[data-cy="interestRate"]`).should('have.value', '15037.59');

      cy.get(`[data-cy="paymentTermMonths"]`).type('30995');
      cy.get(`[data-cy="paymentTermMonths"]`).should('have.value', '30995');

      cy.get(`[data-cy="applicationDate"]`).type('2025-02-04');
      cy.get(`[data-cy="applicationDate"]`).blur();
      cy.get(`[data-cy="applicationDate"]`).should('have.value', '2025-02-04');

      cy.get(`[data-cy="status"]`).type('24395');
      cy.get(`[data-cy="status"]`).should('have.value', '24395');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        loan = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', loanPageUrlPattern);
    });
  });
});
