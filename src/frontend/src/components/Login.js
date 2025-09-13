import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import { login as loginService } from "../services/authService";
import "./Login.css";

function Login() {
  const [step, setStep] = useState(0);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const { login: loginContext } = useContext(AuthContext); // use inside the component

  const validateUsername = () => {
    if (!username.trim()) {
      setError("Username is required");
      return false;
    }
    setError("");
    return true;
  };

  const validatePassword = () => {
    if (!password) {
      setError("Password is required");
      return false;
    }
    setError("");
    return true;
  };

  const handleNext = async () => {
    if (step === 0) {
      if (!validateUsername()) return;
      setStep(1);
    } else {
      if (!validatePassword()) return;
      try {
        const response = await loginService(username, password); // returns { message, token, userJson }
        if (response.token) {
          loginContext(response.token, response.userJson); // save token and user
          navigate("/inbox");
        } else {
          setError("Wrong username or password");
        }
      } catch (error) {
        console.error("Login error:", error);
        setError("Login failed");
        // setError("Login failed: " + error.message);
      }
    }
  };

  return (
    <div className="auth-wrapper login-wrapper">
      <div
        className="auth-slider login-slider"
        style={{ transform: `translateX(-${step * 50}%)` }}
      >
        {/* Username Panel */}
        <div className="auth-panel">
          <h2>Sign in to continue to Email</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          {error && step === 0 && <p className="error-text">{error}</p>}
          <div className="button-row">
            <button onClick={() => navigate("/register")}>
              Create Account
            </button>
            <button onClick={handleNext}>Next</button>
          </div>
        </div>

        {/* Password Panel */}
        <div className="auth-panel">
          <h2>Enter Password</h2>
          <div className="password-group">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <button
              className="show-hide"
              onClick={() => setShowPassword((prev) => !prev)}
            >
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>
          {error && step === 1 && <p className="error-text">{error}</p>}
          <div className="button-row">
            <button onClick={() => setStep(0)}>Back</button>
            <button onClick={handleNext}>Sign In</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
