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

describe('UserData e2e test', () => {
  const userDataPageUrl = '/user-data';
  const userDataPageUrlPattern = new RegExp('/user-data(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userDataSample = { salary: 29035.38, familyLoad: 15152, workplace: 'against', yearsOfEmployment: 25921, employmentStatus: 24982 };

  let userData;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-data+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-data').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-data/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userData) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-data/${userData.id}`,
      }).then(() => {
        userData = undefined;
      });
    }
  });

  it('UserData menu should load UserData page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-data');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserData').should('exist');
    cy.url().should('match', userDataPageUrlPattern);
  });

  describe('UserData page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userDataPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserData page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-data/new$'));
        cy.getEntityCreateUpdateHeading('UserData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDataPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-data',
          body: userDataSample,
        }).then(({ body }) => {
          userData = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-data+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userData],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(userDataPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserData page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userData');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDataPageUrlPattern);
      });

      it('edit button click should load edit UserData page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDataPageUrlPattern);
      });

      it('edit button click should load edit UserData page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserData');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDataPageUrlPattern);
      });

      it('last delete button click should delete instance of UserData', () => {
        cy.intercept('GET', '/api/user-data/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('userData').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userDataPageUrlPattern);

        userData = undefined;
      });
    });
  });

  describe('new UserData page', () => {
    beforeEach(() => {
      cy.visit(`${userDataPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserData');
    });

    it('should create an instance of UserData', () => {
      cy.get(`[data-cy="salary"]`).type('3300.22');
      cy.get(`[data-cy="salary"]`).should('have.value', '3300.22');

      cy.get(`[data-cy="familyLoad"]`).type('28428');
      cy.get(`[data-cy="familyLoad"]`).should('have.value', '28428');

      cy.get(`[data-cy="workplace"]`).type('hm');
      cy.get(`[data-cy="workplace"]`).should('have.value', 'hm');

      cy.get(`[data-cy="housingType"]`).type('readies');
      cy.get(`[data-cy="housingType"]`).should('have.value', 'readies');

      cy.get(`[data-cy="rentCost"]`).type('15372.29');
      cy.get(`[data-cy="rentCost"]`).should('have.value', '15372.29');

      cy.get(`[data-cy="yearsOfEmployment"]`).type('8938');
      cy.get(`[data-cy="yearsOfEmployment"]`).should('have.value', '8938');

      cy.get(`[data-cy="employmentStatus"]`).type('26870');
      cy.get(`[data-cy="employmentStatus"]`).should('have.value', '26870');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userData = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userDataPageUrlPattern);
    });
  });
});
