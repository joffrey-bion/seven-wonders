import React from 'react';
import { Button, Close, Input, Overlay, Panel, PanelFooter, PanelHeader, Space } from 'rebass';

export const Modal = ({ modalOpen, toggleModal }) => (
  <Overlay open={modalOpen} onDismiss={toggleModal('usernameModal')}>
    <Panel theme="info">
      <PanelHeader>
        What's your username ?
        <Space auto />
        <Close onClick={toggleModal('usernameModal')} />
      </PanelHeader>
      <Input label="Username" name="username" placeholder="Cesar92" rounded type="text" />
      <PanelFooter>
        <Space auto />
        <Button theme="success" onClick={toggleModal('usernameModal')} children="Ok" />
      </PanelFooter>
    </Panel>
  </Overlay>
);
