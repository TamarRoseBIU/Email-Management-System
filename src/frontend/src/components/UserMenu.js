import React, { useContext, useState, useRef, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import ThemeToggle from "./ThemeToggle";
import "./UserMenu.css";
// import ProfileModal from "./ProfileModal";
const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL = `http://localhost:${port}`;

function UserMenu() {
  const { logout, user } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(false);
  const [showProfile, setShowProfile] = useState(false);
  const menuRef = useRef(null);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const handleDetails = () => {
    navigate("/profile"); // navigate to the profile details page
    setOpen(false);
  };

  useEffect(() => {
    const close = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("click", close);
    return () => document.removeEventListener("click", close);
  }, []);

  return (
    <div className="user-menu" ref={menuRef}>
      <div className="user-menu-container">
        <ThemeToggle /> {/* Place toggle to the left */}
        <img
          src={
            user?.profilePic
              ? `${BASE_URL}${user.profilePic}`
              : "/default-avatar.png"
          }
          alt="Profile"
          className="user-avatar"
          onClick={() => setOpen(!open)}
        />
      </div>
      {open && (
        <div className="user-menu-modal">
          <div className="user-menu-header">
            <img
              src={
                user?.profilePic
                  ? `${BASE_URL}${user.profilePic}`
                  : "/default-avatar.png"
              }
              alt="Profile"
              className="user-menu-avatar"
            />
            <h3 className="user-menu-name">
              Hello {user?.firstName || "User"}!
            </h3>
          </div>
          <div className="user-menu-actions">
            <button onClick={handleDetails} className="menu-action-button">
              Details
            </button>
            <button
              onClick={handleLogout}
              className="menu-action-button logout"
            >
              Logout
            </button>
          </div>
        </div>
      )}
      {showProfile && (
        <div
          className={`modal-overlay ${showProfile ? "active" : ""}`}
          onClick={() => setShowProfile(false)}
        >
          <ProfileModal onClose={() => setShowProfile(false)} />
        </div>
      )}
    </div>
  );
}

export default UserMenu;
