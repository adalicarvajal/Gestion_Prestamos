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

describe('Alert e2e test', () => {
  const alertPageUrl = '/alert';
  const alertPageUrlPattern = new RegExp('/alert(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const alertSample = { alertType: 'aw cope yearningly', emailSent: true };

  let alert;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/alerts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/alerts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/alerts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (alert) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/alerts/${alert.id}`,
      }).then(() => {
        alert = undefined;
      });
    }
  });

  it('Alerts menu should load Alerts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('alert');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Alert').should('exist');
    cy.url().should('match', alertPageUrlPattern);
  });

  describe('Alert page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(alertPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Alert page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/alert/new$'));
        cy.getEntityCreateUpdateHeading('Alert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', alertPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/alerts',
          body: alertSample,
        }).then(({ body }) => {
          alert = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/alerts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/alerts?page=0&size=20>; rel="last",<http://localhost/api/alerts?page=0&size=20>; rel="first"',
              },
              body: [alert],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(alertPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Alert page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('alert');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', alertPageUrlPattern);
      });

      it('edit button click should load edit Alert page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Alert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', alertPageUrlPattern);
      });

      it('edit button click should load edit Alert page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Alert');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', alertPageUrlPattern);
      });

      it('last delete button click should delete instance of Alert', () => {
        cy.intercept('GET', '/api/alerts/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('alert').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', alertPageUrlPattern);

        alert = undefined;
      });
    });
  });

  describe('new Alert page', () => {
    beforeEach(() => {
      cy.visit(`${alertPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Alert');
    });

    it('should create an instance of Alert', () => {
      cy.get(`[data-cy="alertType"]`).type('for');
      cy.get(`[data-cy="alertType"]`).should('have.value', 'for');

      cy.get(`[data-cy="alertTime"]`).type('2025-02-03T23:51');
      cy.get(`[data-cy="alertTime"]`).blur();
      cy.get(`[data-cy="alertTime"]`).should('have.value', '2025-02-03T23:51');

      cy.get(`[data-cy="emailSent"]`).should('not.be.checked');
      cy.get(`[data-cy="emailSent"]`).click();
      cy.get(`[data-cy="emailSent"]`).should('be.checked');

      cy.get(`[data-cy="details"]`).type('blend');
      cy.get(`[data-cy="details"]`).should('have.value', 'blend');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        alert = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', alertPageUrlPattern);
    });
  });
});
