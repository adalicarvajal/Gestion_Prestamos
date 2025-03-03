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

describe('Amortization e2e test', () => {
  const amortizationPageUrl = '/amortization';
  const amortizationPageUrlPattern = new RegExp('/amortization(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const amortizationSample = {
    installmentNumber: 1570,
    dueDate: '2025-02-04',
    remainingBalance: 4878.42,
    principal: 20505.75,
    paymentAmount: 14454.17,
  };

  let amortization;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/amortizations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/amortizations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/amortizations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (amortization) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/amortizations/${amortization.id}`,
      }).then(() => {
        amortization = undefined;
      });
    }
  });

  it('Amortizations menu should load Amortizations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('amortization');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Amortization').should('exist');
    cy.url().should('match', amortizationPageUrlPattern);
  });

  describe('Amortization page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(amortizationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Amortization page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/amortization/new$'));
        cy.getEntityCreateUpdateHeading('Amortization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amortizationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/amortizations',
          body: amortizationSample,
        }).then(({ body }) => {
          amortization = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/amortizations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/amortizations?page=0&size=20>; rel="last",<http://localhost/api/amortizations?page=0&size=20>; rel="first"',
              },
              body: [amortization],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(amortizationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Amortization page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('amortization');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amortizationPageUrlPattern);
      });

      it('edit button click should load edit Amortization page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Amortization');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amortizationPageUrlPattern);
      });

      it('edit button click should load edit Amortization page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Amortization');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amortizationPageUrlPattern);
      });

      it('last delete button click should delete instance of Amortization', () => {
        cy.intercept('GET', '/api/amortizations/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('amortization').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amortizationPageUrlPattern);

        amortization = undefined;
      });
    });
  });

  describe('new Amortization page', () => {
    beforeEach(() => {
      cy.visit(`${amortizationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Amortization');
    });

    it('should create an instance of Amortization', () => {
      cy.get(`[data-cy="installmentNumber"]`).type('22858');
      cy.get(`[data-cy="installmentNumber"]`).should('have.value', '22858');

      cy.get(`[data-cy="dueDate"]`).type('2025-02-04');
      cy.get(`[data-cy="dueDate"]`).blur();
      cy.get(`[data-cy="dueDate"]`).should('have.value', '2025-02-04');

      cy.get(`[data-cy="remainingBalance"]`).type('9242.12');
      cy.get(`[data-cy="remainingBalance"]`).should('have.value', '9242.12');

      cy.get(`[data-cy="principal"]`).type('5141.1');
      cy.get(`[data-cy="principal"]`).should('have.value', '5141.1');

      cy.get(`[data-cy="paymentDate"]`).type('2025-02-04');
      cy.get(`[data-cy="paymentDate"]`).blur();
      cy.get(`[data-cy="paymentDate"]`).should('have.value', '2025-02-04');

      cy.get(`[data-cy="paymentAmount"]`).type('25923.44');
      cy.get(`[data-cy="paymentAmount"]`).should('have.value', '25923.44');

      cy.get(`[data-cy="penaltyInterest"]`).type('20509.17');
      cy.get(`[data-cy="penaltyInterest"]`).should('have.value', '20509.17');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        amortization = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', amortizationPageUrlPattern);
    });
  });
});
