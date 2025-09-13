import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";
import { markAsReadByType, markAsUnreadByType, deleteItemByType, markAsSpamByType, toggleStarByType } from "./ButtonsByType";



const Inbox = () => {
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user, token } = useContext(AuthContext);

  const [selectedForLabels, setSelectedForLabels] = useState(null);

  useEffect(() => {
    const fetchEmails = async () => {
      const port = process.env.REACT_APP_BACKEND_PORT;
      const API_URL = `http://localhost:${port}/api/mails`;

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
          throw new Error(`Failed to fetch emails: ${response.statusText}`);
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

    fetchEmails();
    const intervalId = setInterval(fetchEmails, 2000);

    return () => clearInterval(intervalId);
  }, [user, token, emails]);

  const handleBackAndMarkRead = async () => {
    if (!selectedEmail?.id) return;
    try {
      await markAsReadByType(selectedEmail.id, token, emails, setEmails);
    } catch (err) {
      alert(`Failed to mark email as read. ${err.message}`);
    } finally {
      setSelectedEmail(null);
    }
  };


  const unreadEmails = emails.filter((email) => !email.isRead);
  const readEmails = emails.filter((email) => email.isRead);

  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={handleBackAndMarkRead}>
          ← Back to Inbox
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
      <h2>📥 Inbox</h2>
      {loading && <p>Loading emails...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        <>
          <h3 className="section-title">Unread</h3>
          {unreadEmails.length === 0 ? (
            <p className="empty-section">No unread emails.</p>
          ) : (
            <ul className="email-list">
              {unreadEmails.map((email) => (
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
                      className="email-item unread"
                      onClick={() => setSelectedEmail(email)}
                    >
                      <span className="sender">{email.from}</span>
                      <div className="subject-fixed-wrapper">
                        <span className="subject"><strong>{email.subject || "(No Subject)"}</strong></span>
                      </div>
                      <span className="body">{email.body}</span>
                      {/*<span className="labels">
                        {email.labels?.length > 0
                          ? email.labels.join(", ")
                          : "No labels"}
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

                    <button
                      className="mark-read-button"
                      title="Mark as Read"
                      onClick={() => markAsReadByType(email.id, token, emails, setEmails)}
                    >
                      📩
                    </button>

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
          )}

          <h3 className="section-title">Everything else</h3>
          {readEmails.length === 0 ? (
            <p className="empty-section">No other emails.</p>
          ) : (
            <ul className="email-list">
              {readEmails.map((email) => (
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
                      className="email-item"
                      onClick={() => setSelectedEmail(email)}
                    >
                      <span className="sender">{email.from}</span>
                      <div className="subject-fixed-wrapper">
                        <span className="subject"><strong>{email.subject || "(No Subject)"}</strong></span>
                      </div>
                      <span className="body">{email.body}</span>
                      {/* <span className="labels">
                        {email.labels?.length > 0
                          ? email.labels.join(", ")
                          : "No labels"}
                      </span> */}
                      <span className="labels">
                        {email.labels?.length > 0
                          ? email.labels.map((label, index) => (
                            <span key={index} className="label-chip">{label}</span>
                          ))
                          : <span className="no-label">No labels</span>}
                      </span>

                      <span className="timestamp">{email.timeStamp}</span>
                    </button>

                    <button
                      className="mark-unread-button"
                      title="Mark as Unread"
                      onClick={() => markAsUnreadByType(email.id, token, emails, setEmails)}
                    >
                      ✉️
                    </button>

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
          )}
        </>
      )}
    </div>
  );
};

export default Inbox;