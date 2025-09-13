import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";
import {markAsReadByType, markAsUnreadByType, deleteItemByType, markAsSpamByType, toggleStarByType} from "./ButtonsByType";
import  MarkAsSpamButton from "./MarkAsSpamButton";


const SentMails = () => {
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user, token } = useContext(AuthContext);
  const [selectedForLabels, setSelectedForLabels] = useState(null);

  useEffect(() => {
    const fetchSentEmails = async () => {
      const port = process.env.REACT_APP_BACKEND_PORT;
      const API_URL = `http://localhost:${port}/api/mails/sent`;

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
          throw new Error(`Failed to fetch sent emails: ${response.statusText}`);
        }

        const data = await response.json();
        if (JSON.stringify(data) !== JSON.stringify(emails)) {
          setEmails(data);
        }

        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchSentEmails();
    const intervalId = setInterval(fetchSentEmails, 3000);
    return () => clearInterval(intervalId);
  }, [user, token, emails]);

  
  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={() => setSelectedEmail(null)}>
            ← Back to Sent Mails
        </button>

        <div className="email-meta-row">
            <span><strong>From: </strong>{selectedEmail.from}</span>
            <span><strong>To: </strong>{Array.isArray(selectedEmail.to) ? selectedEmail.to.join(", ") : selectedEmail.to}</span>
            <span><strong>Timestamp: </strong>{selectedEmail.timeStamp}</span>
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
      <h2>📤 Sent Mails</h2>
      {loading && <p>Loading sent emails...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        emails.length === 0 ? (
          <p className="empty-section">No sent emails.</p>
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
                    <span className="to">
                      <strong>To:</strong> {Array.isArray(email.to) ? email.to.join(", ") : email.to}
                    </span>
                    <div className="subject-fixed-wrapper">
                        <span className="subject"><strong>{email.subject || "(No Subject)"}</strong></span>
                      </div>
                    <span className="body">{email.body}</span>
                    {/*<span className="labels">
                      {email.labels?.length > 0 ? email.labels.join(", ") : "No labels"}
                    </span>*/}
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

export default SentMails;
