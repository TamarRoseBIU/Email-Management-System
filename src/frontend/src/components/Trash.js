import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";
import {
  markAsReadByType,
  markAsUnreadByType,
  deleteItemByType,
  markAsSpamByType,
} from "./ButtonsByType";

const Trash = () => {
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [selectedForLabels, setSelectedForLabels] = useState(null);
  const { user } = useContext(AuthContext);
  const token = user?.token;

  useEffect(() => {
    //const port = localStorage.getItem("api-port") || "8080";
    const port = process.env.REACT_APP_BACKEND_PORT;

    if (!token) {
      setError("User not logged in or token missing.");
      setLoading(false);
      return;
    }

    const API_URL = `http://localhost:${port}/api/trash`;

    const fetchTrashEmails = () => {
      fetch(API_URL, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
        .then((res) => {
          if (!res.ok) throw new Error("Failed to fetch trash.");
          return res.json();
        })
        .then((data) => {
          setEmails(data);
          setLoading(false);
        })
        .catch((err) => {
          setError(err.message);
          setLoading(false);
        });
    };

    fetchTrashEmails();
    const intervalId = setInterval(fetchTrashEmails, 2000);
    return () => clearInterval(intervalId);
  }, [token]);

  const handleRestore = async (id) => {
    //const port = localStorage.getItem("api-port") || "8080";
    const port = process.env.REACT_APP_BACKEND_PORT;
    try {
      await fetch(`http://localhost:${port}/api/trash/restore/${id}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setEmails((prev) => prev.filter((email) => email.id !== id));
    } catch (err) {
      alert(`Failed to restore mail: ${err.message}`);
    }
  };

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

  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={handleBackAndMarkRead}>
          ← Back to Trash
        </button>

        <div className="email-meta-row">
          <span>
            <strong>From:</strong> {selectedEmail.from}
          </span>
          <span>
            <strong>To:</strong>{" "}
            {Array.isArray(selectedEmail.to)
              ? selectedEmail.to.join(", ")
              : selectedEmail.to}
          </span>
          <span>
            <strong>Timestamp:</strong> {selectedEmail.timeStamp}
          </span>
          <span>
            <strong>Labels:</strong>{" "}
            {selectedEmail.labels?.length > 0
              ? selectedEmail.labels.join(", ")
              : "No labels"}
          </span>
        </div>

        <p className="email-subject">
          <strong>{selectedEmail.subject}</strong>
        </p>
        <hr />
        <p className="email-body">{selectedEmail.body}</p>
      </div>
    );
  }

  return (
    <div className="inbox-container">
      <h2>🗑️ Trash</h2>
      {loading && <p>Loading trash...</p>}
      {error && <p className="error">{error}</p>}

      {!loading &&
        !error &&
        (emails.length === 0 ? (
          <p className="empty-section">Trash is empty.</p>
        ) : (
          <ul className="email-list">
            {emails.map((email) => (
              <li key={email.id}>
                <div className="email-item-wrapper">
                  <button
                    className={`email-item ${!email.isRead ? "unread" : ""}`}
                    onClick={() => setSelectedEmail(email)}
                  >
                    <span className="sender">{email.from}</span>
                    <div className="subject-fixed-wrapper">
                      {email.trashSource === "drafts" && (
                        <span className="draft-absolute">Draft</span>
                      )}
                      <span className="subject">
                        <strong>{email.subject || "(No Subject)"}</strong>
                      </span>
                    </div>
                    <span className="body">{email.body}</span>
                    <span className="labels">
                      {email.labels?.length > 0 ? (
                        email.labels.map((label, index) => (
                          <span key={index} className="label-chip">
                            {label}
                          </span>
                        ))
                      ) : (
                        <span className="no-label">No labels</span>
                      )}
                    </span>
                    <span className="timestamp">{email.timeStamp}</span>
                  </button>

                  {email.isRead ? (
                    <button
                      className="mark-unread-button"
                      title="Mark as Unread"
                      onClick={() =>
                        markAsUnreadByType(email.id, token, emails, setEmails)
                      }
                    >
                      ✉️
                    </button>
                  ) : (
                    <button
                      className="mark-read-button"
                      title="Mark as Read"
                      onClick={() =>
                        markAsReadByType(email.id, token, emails, setEmails)
                      }
                    >
                      📩
                    </button>
                  )}

                  <button
                    className="restore-button"
                    title="Restore from Trash"
                    onClick={() => handleRestore(email.id)}
                  >
                    ↩️
                  </button>

                  <button
                    className="trash-button"
                    title="Delete permanently"
                    onClick={() =>
                      deleteItemByType(email.id, token, emails, setEmails)
                    }
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
                    onClick={() =>
                      markAsSpamByType(email.id, token, emails, setEmails)
                    }
                  >
                    🚫
                  </button>
                </div>
              </li>
            ))}
          </ul>
        ))}
    </div>
  );
};

export default Trash; //
