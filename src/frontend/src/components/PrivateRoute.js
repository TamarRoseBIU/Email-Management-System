import React, { useContext, useEffect } from "react";
import { Navigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { isTokenExpired } from "../services/jwtUtils";

const Spinner = () => (
  <div style={{ textAlign: "center", padding: "2rem" }}>Loading…</div>
);

const PrivateRoute = ({ children }) => {
  const { token, loading, logout } = useContext(AuthContext);

  // Check on mount
  useEffect(() => {
    if (token && isTokenExpired(token)) {
      logout();
    }
  }, [token, logout]);

  // Check continuously every 5 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      if (token && isTokenExpired(token)) {
        logout();
      }
    }, 5); // every 5 seconds

    return () => clearInterval(interval); // cleanup
  }, [token, logout]);

  if (loading) return <Spinner />;
  if (!token) return <Navigate to="/login" replace />;
  return children;
};

export default PrivateRoute;
