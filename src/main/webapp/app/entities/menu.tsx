import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate } from 'react-jhipster';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/amortization">
        <Translate contentKey="global.menu.entities.amortization" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/loan">
        <Translate contentKey="global.menu.entities.loan" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/user-data">
        <Translate contentKey="global.menu.entities.userData" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
