import React, { useState, useEffect, useContext, useCallback } from "react";
import { AuthContext } from "../context/AuthContext";
import "./MailsView.css";
import LabelSelectorButton from "./LabelSelectorButton";
import {
  markAsReadByType,
  markAsUnreadByType,
  deleteItemByType,
  markAsSpamByType,
  toggleStarByType,
} from "./ButtonsByType";

export default function SearchResult({ type, term }) {
  const { user, token } = useContext(AuthContext);
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedForLabels, setSelectedForLabels] = useState(null);

  // New state for detailed view
  const [selectedEmail, setSelectedEmail] = useState(null);

  const fetchResults = useCallback(async () => {
    if (!user || !token) {
      setError("User not logged in or token missing.");
      setLoading(false);
      return;
    }
    try {
      const port = process.env.REACT_APP_BACKEND_PORT;
      const url =
        type === "query"
          ? `http://localhost:${port}/api/searchAll/query/${encodeURIComponent(
              term
            )}`
          : `http://localhost:${port}/api/searchAll/label/${encodeURIComponent(
              term
            )}`;

      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      if (!response.ok)
        throw new Error(`HTTP error! status: ${response.status}`);
      const { data = [] } = await response.json();
      setResults(data);
    } catch (e) {
      console.error("Fetch error:", e);
      setError(e.message);
      setResults([]);
    } finally {
      setLoading(false);
    }
  }, [type, term, token, user]);

  useEffect(() => {
    fetchResults();
    const intervalId = setInterval(fetchResults, 2000);
    return () => clearInterval(intervalId);
  }, [fetchResults]);

  const handleToggleRead = async (item) => {
    try {
      if (item.isRead) {
        await markAsUnreadByType(item.id, token, results, setResults);
      } else {
        await markAsReadByType(item.id, token, results, setResults);
      }
    } catch (e) {
      console.error("Error toggling read status:", e);
    }
  };

  const handleDelete = async (item) => {
    try {
      await deleteItemByType(item.id, token, results, setResults);
    } catch (e) {
      console.error("Error deleting item:", e);
    }
  };

  const handleStar = (item) => async () => {
    try {
      await toggleStarByType(
        item.id,
        item.isStarred,
        token,
        results,
        setResults
      );
    } catch (e) {
      console.error("Error toggling star:", e);
    }
  };

  const handleSpam = (item) => async () => {
    try {
      await markAsSpamByType(item.id, token, results, setResults);
    } catch (e) {
      console.error("Error marking as spam:", e);
    }
  };

  // Handler for opening an email detail
  const openEmail = (item) => {
    setSelectedEmail(item);
  };

  // Back button handler, mark as read and close detail
  const handleBackAndMarkRead = async () => {
    if (!selectedEmail?.id) return;
    try {
      await markAsReadByType(selectedEmail.id, token, results, setResults);
    } catch (err) {
      console.error(`Failed to mark email as read. ${err.message}`);
    } finally {
      setSelectedEmail(null);
    }
  };

  // Render detailed view if an email is selected
  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={handleBackAndMarkRead}>
          ← Back to Results
        </button>
        <div className="email-meta-row">
          <span>
            <strong>From: </strong>
            {selectedEmail.from}
          </span>
          <span>
            <strong>To: </strong>
            {Array.isArray(selectedEmail.to)
              ? selectedEmail.to.join(", ")
              : selectedEmail.to}
          </span>
          <span>
            <strong>Timestamp: </strong>
            {selectedEmail.timeStamp}
          </span>
          <span>
            <strong>Labels: </strong>
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

  // Otherwise, render the list view
  return (
    <div className="inbox-container">
      <h2>
        🔍 {type === "query" ? `Results for "${term}"` : `Label: ${term}`}
      </h2>
      {loading && <p>Loading results...</p>}
      {error && <p className="error">{error}</p>}
      {!loading && !error && (
        <>
          {results.length === 0 ? (
            <p className="empty-section">No results found.</p>
          ) : (
            <ul className="email-list">
              {results.map((item) => (
                <li key={item.id}>
                  <div className="email-item-wrapper">
                    <button
                      className="star-button"
                      title={item.isStarred ? "Unstar" : "Star"}
                      onClick={handleStar(item)}
                    >
                      {item.isStarred ? "★" : "☆"}
                    </button>
                    {/* Clickable area to open detail */}
                    <button
                      className={`email-item ${!item.isRead ? "unread" : ""}`}
                      onClick={() => openEmail(item)}
                    >
                      <span className="sender">{item.from}</span>
                      <div className="subject-fixed-wrapper">
                        {item.source === "drafts" && (
                          <span className="draft-absolute">Draft</span>
                        )}
                        <span className="subject">
                          <strong>{item.subject || "(No Subject)"}</strong>
                        </span>
                      </div>
                      <span className="body">{item.body}</span>
                      <span className="labels">
                        {item.labels?.length ? (
                          item.labels.map((lbl, i) => (
                            <span key={i} className="label-chip">
                              {lbl}
                            </span>
                          ))
                        ) : (
                          <span className="no-label">No labels</span>
                        )}
                      </span>
                      <span className="timestamp">{item.timeStamp}</span>
                    </button>
                    <button
                      className={`mark-${
                        item.isRead ? "unread" : "read"
                      }-button`}
                      onClick={() => handleToggleRead(item)}
                      title={item.isRead ? "Mark as Unread" : "Mark as Read"}
                    >
                      {item.isRead ? "✉️" : "📩"}
                    </button>
                    <button
                      className="trash-button"
                      onClick={() => handleDelete(item)}
                      title="Delete"
                    >
                      🗑️
                    </button>
                    <button
                      className="spam-button"
                      onClick={handleSpam(item)}
                      title="Mark as Spam"
                    >
                      🚫
                    </button>
                    {selectedForLabels === item.id ? (
                      <LabelSelectorButton
                        mailId={item.id}
                        currentLabels={item.labels}
                        token={token}
                        onClose={() => setSelectedForLabels(null)}
                        onUpdate={(newLabels) => {
                          setResults((rs) =>
                            rs.map((r) =>
                              r.id === item.id ? { ...r, labels: newLabels } : r
                            )
                          );
                        }}
                      />
                    ) : (
                      <button
                        className="labels-button"
                        title="Labels"
                        onClick={() => setSelectedForLabels(item.id)}
                      >
                        🏷️
                      </button>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}
