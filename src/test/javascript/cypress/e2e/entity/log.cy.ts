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

describe('Log e2e test', () => {
  const logPageUrl = '/log';
  const logPageUrlPattern = new RegExp('/log(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const logSample = { eventType: 'bludgeon' };

  let log;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (log) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/logs/${log.id}`,
      }).then(() => {
        log = undefined;
      });
    }
  });

  it('Logs menu should load Logs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Log').should('exist');
    cy.url().should('match', logPageUrlPattern);
  });

  describe('Log page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(logPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Log page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/log/new$'));
        cy.getEntityCreateUpdateHeading('Log');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', logPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/logs',
          body: logSample,
        }).then(({ body }) => {
          log = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/logs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/logs?page=0&size=20>; rel="last",<http://localhost/api/logs?page=0&size=20>; rel="first"',
              },
              body: [log],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(logPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Log page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('log');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', logPageUrlPattern);
      });

      it('edit button click should load edit Log page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Log');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', logPageUrlPattern);
      });

      it('edit button click should load edit Log page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Log');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', logPageUrlPattern);
      });

      it('last delete button click should delete instance of Log', () => {
        cy.intercept('GET', '/api/logs/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('log').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', logPageUrlPattern);

        log = undefined;
      });
    });
  });

  describe('new Log page', () => {
    beforeEach(() => {
      cy.visit(`${logPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Log');
    });

    it('should create an instance of Log', () => {
      cy.get(`[data-cy="eventType"]`).type('underachieve to psst');
      cy.get(`[data-cy="eventType"]`).should('have.value', 'underachieve to psst');

      cy.get(`[data-cy="eventTime"]`).type('2025-02-04T08:52');
      cy.get(`[data-cy="eventTime"]`).blur();
      cy.get(`[data-cy="eventTime"]`).should('have.value', '2025-02-04T08:52');

      cy.get(`[data-cy="ipAddress"]`).type('cafe famously');
      cy.get(`[data-cy="ipAddress"]`).should('have.value', 'cafe famously');

      cy.get(`[data-cy="description"]`).type('newsprint fatal consequently');
      cy.get(`[data-cy="description"]`).should('have.value', 'newsprint fatal consequently');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        log = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', logPageUrlPattern);
    });
  });
});
