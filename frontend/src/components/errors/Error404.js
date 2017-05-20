import React from "react";
import { Link } from "react-router";

const Error404 = () => (
  <div>
    <h1>No Match</h1>
    <Link to="/">
      Take me back home ! <span role="img" aria-label="home">🏠</span>
    </Link>
  </div>
);
export default Error404;
