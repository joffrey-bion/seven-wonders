import React from 'react'
import {
  Overlay,
  Panel,
  PanelHeader,
  PanelFooter,
  Button,
  Input,
  Close,
  Space
} from 'rebass'

const Modal = ({ modalOpen, toggleModal }) => (
  <Overlay open={modalOpen} onDismiss={toggleModal('usernameModal')}>
    <Panel theme="info">
      <PanelHeader>
        What's your username ?
        <Space auto />
        <Close onClick={toggleModal('usernameModal')} />
      </PanelHeader>
      <Input
        label="Username"
        name="username"
        placeholder="Cesar92"
        rounded
        type="text"
      />
      <PanelFooter>
        <Space auto />
        <Button
          theme="success"
          onClick={toggleModal('usernameModal')}
          children="Ok"
        />
      </PanelFooter>
    </Panel>
  </Overlay>
)

export default Modal
