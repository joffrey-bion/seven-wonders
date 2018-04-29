import React from 'react';
import ReduxToastr from 'react-redux-toastr';
import './react-redux-toastr.min.css';

export const ErrorToastContainer = props => (
  <ReduxToastr timeOut={4000} preventDuplicates position="bottom-left" progressBar />
);
