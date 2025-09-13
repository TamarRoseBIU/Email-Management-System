import React, { useContext } from "react";
import { ThemeContext } from "./ThemeContext";
import "./ThemeToggle.css";

const ThemeToggle = () => {
  const { theme, toggleTheme } = useContext(ThemeContext);

  return (
    <label className="theme-toggle-container">
      <input
        type="checkbox"
        checked={theme === "dark"}
        onChange={toggleTheme}
        aria-label={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
      />
      <span className="theme-toggle-button"></span>
    </label>
  );
};

export default ThemeToggle;
