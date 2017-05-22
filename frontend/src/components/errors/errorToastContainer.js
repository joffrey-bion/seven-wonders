import React from 'react';
import ReduxToastr from 'react-redux-toastr';
import './react-redux-toastr.min.css';

const ErrorToastContainer = props => (
  <ReduxToastr
    timeOut={4000}
    preventDuplicates
    position="bottom-left"
    progressBar
  />
);

export default ErrorToastContainer;
