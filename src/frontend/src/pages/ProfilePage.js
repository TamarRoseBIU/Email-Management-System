import React, { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../context/AuthContext";
import "../components/Profile.css";

const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL = `http://localhost:${port}`;

function ProfileDetails() {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  return (
    <div className="profile-details-container">
      <div className="profile-details">
        <button className="close-button" onClick={() => navigate(-1)}>
          &times;
        </button>

        <div className="profile-header">
          <img
            src={
              user?.profilePic
                ? `${BASE_URL}${user.profilePic}`
                : "/default-avatar.png"
            }
            alt="Profile"
            className="profile-details-avatar"
          />
          <h2 className="profile-details-name">
            {user?.firstName || "User"} {user?.lastName || ""}
          </h2>
        </div>

        <div className="profile-details-info">
          <div className="data-pair">
            <span className="data-label">Username:</span>
            <span className="data-value">
              {user?.username || "Not specified"}
            </span>
          </div>

          <div className="data-pair">
            <span className="data-label">Phone Number:</span>
            <span className="data-value">
              {user?.phoneNumber || "Not specified"}
            </span>
          </div>

          <div className="data-pair">
            <span className="data-label">Birth Date:</span>
            <span className="data-value">
              {user?.birthDate || "Not specified"}
            </span>
          </div>

          <div className="data-pair">
            <span className="data-label">Gender:</span>
            <span className="data-value">
              {user?.gender || "Not specified"}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProfileDetails;
