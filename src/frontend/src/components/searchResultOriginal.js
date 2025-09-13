import React, { useState, useContext } from "react";
import "./searchResult.css";
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

  // Sync emails if prop changes
  React.useEffect(() => {
    setEmails(initialEmails || []);
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

  return (
    <div className="search-results-container">
      <h2 className="title">🔎 Search Results</h2>
      {emails.length === 0 ? (
        <p className="no-results">No results found for "{query}"</p>
      ) : (
        <ul className="email-list">
          {emails.map((email, idx) => (
            <li key={email.id || idx} className="email-item">
              <button
                className="star-button"
                title={email.isStarred ? "Unstar" : "Star"}
                onClick={() => handleToggleStar(email)}
              >
                {email.isStarred ? "★" : "☆"}
              </button>

              <div className={`email-content ${!email.isRead ? "unread" : ""}`}>
                <span className="sender">{email.from || "Unknown"}</span>
                <span className="subject">{email.subject || "No Subject"}</span>
                <span className="preview">{(email.body || "").slice(0, 60)}...</span>
                <span className="time">{email.timeStamp || ""}</span>
              </div>

              <button
                className={email.isRead ? "mark-unread-button" : "mark-read-button"}
                title={email.isRead ? "Mark as Unread" : "Mark as Read"}
                onClick={() => handleMarkRead(email)}
              >
                {email.isRead ? "✉️" : "📩"}
              </button>

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
                  onClose={() => setSelectedForLabels(null)}
                  onUpdate={(newLabels) => {
                    setEmails((prev) =>
                      prev.map((e) =>
                        e.id === email.id ? { ...e, labels: newLabels } : e
                      )
                    );
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
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default SearchResults;
