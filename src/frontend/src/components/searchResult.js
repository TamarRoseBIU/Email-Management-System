import React, { useState, useContext, useEffect } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";
import {
  markAsReadByType,
  markAsUnreadByType,
  deleteItemByType,
  markAsSpamByType,
  toggleStarByType,
} from "./ButtonsByType";

const SearchResults = ({ emails: initialEmails, query }) => {
  const { token } = useContext(AuthContext);
  const [emails, setEmails] = useState(initialEmails || []);
  const [selectedForLabels, setSelectedForLabels] = useState(null);
  const [selectedEmail, setSelectedEmail] = useState(null);

  useEffect(() => {
    setEmails(initialEmails || []);
    setSelectedEmail(null);
  }, [initialEmails]);

  const handleToggleStar = async (email) => {
    await toggleStarByType(email.id, email.isStarred, token, emails, setEmails);
  };

  const handleMarkRead = async (email) => {
    if (email.isRead) {
      await markAsUnreadByType(email.id, token, emails, setEmails);
    } else {
      await markAsReadByType(email.id, token, emails, setEmails);
    }
  };

  const handleDelete = async (email) => {
    await deleteItemByType(email.id, token, emails, setEmails);
  };

  const handleSpam = async (email) => {
    await markAsSpamByType(email.id, token, emails, setEmails);
  };

  const fetchUpdatedEmail = async (id) => {
    try {
      const port = process.env.REACT_APP_BACKEND_PORT;
      const res = await fetch(`http://localhost:${port}/api/mails/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error("Failed to fetch updated mail");
      const updated = await res.json();
      updated.source = "inbox";
      setEmails((prev) =>
        prev.map((e) => (e.id === id ? updated : e))
      );
    } catch (err) {
      console.error("Error fetching updated mail:", err);
    }
  };
  const fetchUpdatedDraft = async (id) => {
    try {
      const port = process.env.REACT_APP_BACKEND_PORT;
      const res = await fetch(`http://localhost:${port}/api/drafts/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error("Failed to fetch updated draft");
      const updated = await res.json();
      updated.source = "drafts";
      setEmails((prev) =>
        prev.map((d) => (d.id === id ? updated : d))
      );
    } catch (err) {
      console.error("Error fetching updated draft:", err);
    }
  };


  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={() => setSelectedEmail(null)}>
          ← Back to Search Results
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
    <div className="search-results-container">
      <h2 className="title">🔎 Search Results</h2>
      {emails.length === 0 ? (
        <p className="no-results">No results found for "{query}"</p>
      ) : (
        <ul className="email-list">
          {emails.map((email) => (
            <li key={email.id}>
              <div className="email-item-wrapper">
                <button
                  className="star-button"
                  title="Star"
                  onClick={() => handleToggleStar(email)}
                >
                  {email.isStarred ? "★" : "☆"}
                </button>

                <button
                  className={`email-item ${!email.isRead ? "unread" : ""}`}
                  onClick={() => setSelectedEmail(email)}
                >
                  <span className="to"><strong>From:</strong> {email.from || "Unknown"}</span>
                  <div className="subject-fixed-wrapper">
                    {email.source === "drafts" && <span className="draft-absolute">Draft</span>}
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
                    onClick={() => handleMarkRead(email)}
                  >
                    ✉️
                  </button>
                ) : (
                  <button
                    className="mark-read-button"
                    title="Mark as Read"
                    onClick={() => handleMarkRead(email)}
                  >
                    📩
                  </button>
                )}

                <button
                  className="trash-button"
                  title="Delete"
                  onClick={() => handleDelete(email)}
                >
                  🗑️
                </button>

                {selectedForLabels === email.id ? (
                  <LabelSelectorButton
                    mailId={email.id}
                    currentLabels={email.labels}
                    token={token}
                    onClose={() => {
                      if (email.source === "drafts") {
                        fetchUpdatedDraft(email.id);
                      } else {
                        fetchUpdatedEmail(email.id);
                      }
                      setSelectedForLabels(null);
                    }}
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
                  onClick={() => handleSpam(email)}
                >
                  🚫
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};


export default SearchResults;
