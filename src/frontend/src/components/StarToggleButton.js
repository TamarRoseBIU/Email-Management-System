
import React from "react";
import "./StarToggleButton.css";

const StarToggleButton = ({ emailId, isStarred, token, emails, setEmails }) => {
  const port = process.env.REACT_APP_BACKEND_PORT;
  const baseUrl = `http://localhost:${port}/api/starred`;

  const toggleStar = async () => {
    const endpoint = isStarred ? `${baseUrl}/unstar/${emailId}` : `${baseUrl}/star/${emailId}`;
    const method = "PATCH";

    try {
      const res = await fetch(endpoint, {
        method,
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Failed to update star status.");

      const updatedEmails = emails.map((email) =>
        email.id === emailId ? { ...email, isStarred: !isStarred } : email
      );
      setEmails(updatedEmails);
    } catch (err) {
      alert(`Failed to ${isStarred ? "remove" : "add"} star: ${err.message}`);
    }
  };

  return (
    <button className="star-button" onClick={toggleStar} title="Star">
      {isStarred ? "★" : "☆"}
    </button>
  );
};

export default StarToggleButton;
