// export default ProfileModal;
import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "./ProfileModal.css";
//const port = localStorage.getItem("api-port") || "8080";
const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL = `http://localhost:${port}`;

function ProfileModal({ onClose }) {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleClose = () => {
    onClose(); // Close the modal
  };

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="profile-modal" onClick={(e) => e.stopPropagation()}>
        <h2 className="profile-title">Profile</h2>
        <img
          src={
            user?.profilePic
              ? `${BASE_URL}${user.profilePic}`
              : "/default-avatar.png"
          }
          alt="Profile"
          className="profile-avatar"
        />

        <p className="profile-detail">
          First Name: {user?.firstName || "Not specified"}
        </p>
        <p className="profile-detail">
          Last Name: {user?.lastName || "Not specified"}
        </p>
        <p className="profile-detail">
          Username: {user?.username || "Not specified"}
        </p>
        <p className="profile-detail">
          Phone Number: {user?.phoneNumber || "Not specified"}
        </p>
        <p className="profile-detail">
          Birth Date: {user?.birthDate || "Not specified"}
        </p>
        <p className="profile-detail">
          Gender: {user?.gender || "Not specified"}
        </p>
        <button className="close-button" onClick={handleClose}>
          Close
        </button>
      </div>
    </div>
  );
}

export default ProfileModal;
