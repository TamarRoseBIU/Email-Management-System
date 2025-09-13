import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";

import {
  markAsReadByType,
  markAsUnreadByType,
  deleteItemByType,
  markAsSpamByType,
  toggleStarByType,
  determineMailType,
} from "./ButtonsByType";

const StarredMails = () => {
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user, token } = useContext(AuthContext);
  const [selectedForLabels, setSelectedForLabels] = useState(null);

  useEffect(() => {
    const fetchStarredEmails = async () => {
      const port = process.env.REACT_APP_BACKEND_PORT;
     // const port = localStorage.getItem("api-port") || "8080";
      const API_URL = `http://localhost:${port}/api/starred`;

      if (!user || !token) {
        setError("User not logged in or token missing.");
        setLoading(false);
        return;
      }

      try {
        const response = await fetch(API_URL, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error(`Failed to fetch starred emails: ${response.statusText}`);
        }

        const data = await response.json();

        const emailsWithType = await Promise.all(
          data.map(async (email) => {
            try {
              const type = await determineMailType(email.id, token);
              if (!type) throw new Error("Type is null");
              return { ...email, type };
            } catch (err) {
              console.error(`Error determining type for email ${email.id}:`, err);
              return { ...email, type: "error" };
            }
          })
        );

        if (JSON.stringify(emailsWithType) !== JSON.stringify(emails)) {
          setEmails(emailsWithType);
        }

        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchStarredEmails();
    const intervalId = setInterval(fetchStarredEmails, 3000);
    return () => clearInterval(intervalId);
  }, [user, token]);

  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={() => setSelectedEmail(null)}>
          ← Back to Starred Mails
        </button>

        <div className="email-meta-row">
          <span><strong>From:</strong> {selectedEmail.from}</span>
          <span><strong>To:</strong> {Array.isArray(selectedEmail.to) ? selectedEmail.to.join(", ") : selectedEmail.to}</span>
          <span><strong>Timestamp:</strong> {selectedEmail.timeStamp}</span>
          <span><strong>Labels:</strong> {selectedEmail.labels?.length > 0 ? selectedEmail.labels.join(", ") : "No labels"}</span>
        </div>

        <p className="email-subject"><strong>{selectedEmail.subject}</strong></p>
        <hr />
        <p className="email-body">{selectedEmail.body}</p>
      </div>
    );
  }

  return (
    <div className="inbox-container">
      <h2>⭐ Starred Mails</h2>
      {loading && <p>Loading starred emails...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        emails.length === 0 ? (
          <p className="empty-section">No starred emails.</p>
        ) : (
          <ul className="email-list">
            {emails.map((email) => (
              <li key={email.id}>
                <div className="email-item-wrapper">
                  <button
                    className="star-button"
                    title="Star"
                    onClick={() => toggleStarByType(email.id, email.isStarred, token, emails, setEmails)}
                  >
                    {email.isStarred ? "★" : "☆"}
                  </button>

                  <button
                    className={`email-item ${!email.isRead ? "unread" : ""}`}
                    onClick={() => setSelectedEmail(email)}
                  >
                    <span className="sender">{email.from}</span>

                    <div className="subject-fixed-wrapper">
                      {email.type === "drafts" && (
                        <span className="draft-absolute">Draft</span>
                      )}
                      <span className="subject"><strong>{email.subject || "(No Subject)"}</strong></span>
                    </div>

                    <span className="body">{email.body}</span>
                    <span className="labels">
                      {email.labels?.length > 0
                        ? email.labels.map((label, index) => (
                          <span key={index} className="label-chip">{label}</span>
                        ))
                        : <span className="no-label">No labels</span>}
                    </span>
                    <span className="timestamp">{email.timeStamp}</span>
                  </button>

                  {email.isRead ? (
                    <button
                      className="mark-unread-button"
                      title="Mark as Unread"
                      onClick={() => markAsUnreadByType(email.id, token, emails, setEmails)}
                    >
                      ✉️
                    </button>
                  ) : (
                    <button
                      className="mark-read-button"
                      title="Mark as Read"
                      onClick={() => markAsReadByType(email.id, token, emails, setEmails)}
                    >
                      📩
                    </button>
                  )}

                  <button
                    className="trash-button"
                    title="Delete"
                    onClick={() => deleteItemByType(email.id, token, emails, setEmails)}
                  >
                    🗑️
                  </button>

                  {selectedForLabels === email.id ? (
                    <LabelSelectorButton
                      mailId={email.id}
                      currentLabels={email.labels}
                      token={token}
                      onClose={() => setSelectedForLabels(null)}
                    />
                  ) : (
                    <button
                      className="labels-button"
                      title="Labels"
                      onClick={() => setSelectedForLabels(email.id)}
                    >
                      🏷️
                    </button>
                  )}

                  <button
                    className="spam-button"
                    title="Mark as Spam"
                    onClick={() => markAsSpamByType(email.id, token, emails, setEmails)}
                  >
                    🚫
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )
      )}
    </div>
  );
};

export default StarredMails;
