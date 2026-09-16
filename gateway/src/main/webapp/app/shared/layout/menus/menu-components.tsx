import React from 'react';
import { Dropdown, DropdownMenu, DropdownToggle, Nav } from 'react-bootstrap';

import { IconProp } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export interface INavDropdown {
  children: React.ReactNode;
  icon: IconProp;
  name?: string;
  id?: string;
  style?: React.CSSProperties;
  'data-cy'?: string;
}

export const NavDropdown = (props: INavDropdown) => (
  <Dropdown as={Nav.Item} id={props.id} data-cy={props['data-cy']}>
    <DropdownToggle as={Nav.Link} className="d-flex align-items-center">
      <FontAwesomeIcon icon={props.icon} />
      <span>{props.name}</span>
    </DropdownToggle>
    <DropdownMenu renderOnMount align="end" style={props.style}>
      {props.children}
    </DropdownMenu>
  </Dropdown>
);
