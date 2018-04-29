import React from 'react';
import { Link } from 'react-router-dom';

export const Error404 = () => (
  <div>
    <h1>No Match</h1>
    <Link to="/">
      Take me back home ! <span role="img" aria-label="home">🏠</span>
    </Link>
  </div>
);
