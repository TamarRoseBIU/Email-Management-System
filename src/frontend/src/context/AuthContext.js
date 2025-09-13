import React, { createContext, useState, useEffect } from "react";
import { getUser } from "../services/authService";
import { decodeJWT } from "../services/jwtUtils";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Compute auth flag
  const isAuthenticated = Boolean(token);

  // On token change, validate token and fetch user quietly
  useEffect(() => {
    if (!token) {
      setUser(null);
      setLoading(false);
      return;
    }

    const checkExpiryAndFetch = async () => {
      // Decode and check expiration
      try {
        const payload = decodeJWT(token);
        if (!payload || payload.exp * 1000 < Date.now()) {
          // Token expired: logout quietly
          localStorage.removeItem("token");
          setToken(null);
          setUser(null);
          setLoading(false);
          return;
        }
      } catch {
        // Invalid token: logout quietly
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
        setLoading(false);
        return;
      }

      // Valid token: fetch user data
      try {
        const userData = await getUser(token);
        if (userData.error) {
          // Backend rejected token: logout quietly
          localStorage.removeItem("token");
          setToken(null);
          setUser(null);
        } else {
          setUser({ ...userData, token });
        }
      } catch {
        // Fetch error: logout quietly
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    checkExpiryAndFetch();
  }, [token]);

  // login: save token and user
  const login = (newToken, userData) => {
    localStorage.setItem("token", newToken);
    setToken(newToken);
    setUser({ ...userData, token: newToken });
  };

  // logout: clear storage and state quietly
  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{ token, user, loading, isAuthenticated, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};
