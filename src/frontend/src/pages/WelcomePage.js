import React from "react";
import { useNavigate } from "react-router-dom";
import "../components/Welcome.css";

export default function WelcomePage() {
  const navigate = useNavigate();

  return (
    <div className="welcome-container">
      <h1 className="welcome-title">Welcome to Email App</h1>
      <div className="welcome-buttons">
        <button className="welcome-button" onClick={() => navigate("/login")}>
          Login
        </button>
        <button
          className="welcome-button"
          onClick={() => navigate("/register")}
        >
          Register
        </button>
      </div>
    </div>
  );
}
