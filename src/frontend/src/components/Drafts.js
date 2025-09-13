import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import ComposeModal from "./ComposeModal";
import LabelSelectorButton from "./LabelSelectorButton";
import { markAsReadByType, markAsUnreadByType, deleteItemByType, markAsSpamByType, toggleStarByType } from "./ButtonsByType";

const Drafts = () => {
  const [drafts, setDrafts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingDraft, setEditingDraft] = useState(null);
  const { token } = useContext(AuthContext);
  const [selectedForLabels, setSelectedForLabels] = useState(null);

  useEffect(() => {
    const port = process.env.REACT_APP_BACKEND_PORT;

    if (!token) {
      setError("User not logged in or token missing.");
      setLoading(false);
      return;
    }

    const API_URL = `http://localhost:${port}/api/drafts`;

    const fetchDrafts = () => {
      fetch(API_URL, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Failed to fetch drafts.");
          }
          return response.json();
        })
        .then((data) => {
          setDrafts(data);
          setLoading(false);
        })
        .catch((err) => {
          setError(err.message);
          setLoading(false);
        });
    };

    fetchDrafts();
    const intervalId = setInterval(fetchDrafts, 2000);
    return () => clearInterval(intervalId);
  }, [token]);

  if (editingDraft) {
    return (
      <ComposeModal
        onClose={() => setEditingDraft(null)}
        initialTo={Array.isArray(editingDraft.to) ? editingDraft.to.join(", ") : editingDraft.to}
        initialSubject={editingDraft.subject}
        initialBody={editingDraft.body}
        initialLabels={editingDraft.labels || []}
        draftId={editingDraft.id}
      />
    );
  }

  return (
    <div className="inbox-container">
      <h2>📄 Drafts</h2>
      {loading && <p>Loading drafts...</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && (
        drafts.length === 0 ? (
          <p className="empty-section">No drafts found.</p>
        ) : (
          <ul className="email-list">
            {drafts.map((draft) => (
              <li key={draft.id}>
                <div className="email-item-wrapper">
                  <button
                    className="star-button"
                    title="Star"
                    onClick={() => toggleStarByType(draft.id, draft.isStarred, token, drafts, setDrafts)}
                  >
                    {draft.isStarred ? "★" : "☆"}
                  </button>

                  <button
                    className={`email-item ${!draft.isRead ? "unread" : ""}`}
                    onClick={() => {
                      setEditingDraft(draft);
                      if (!draft.isRead) markAsReadByType(draft.id, token, drafts, setDrafts);
                    }}
                  >
                    <span className="sender">{draft.from}</span>
                    <div className="subject-fixed-wrapper">
                      <span className="subject"><strong>{draft.subject || "(No Subject)"}</strong></span>
                    </div>
                    <span className="body">{draft.body}</span>
                    <span className="labels">
                      {draft.labels?.length > 0
                        ? draft.labels.map((label, index) => (
                          <span key={index} className="label-chip">{label}</span>
                        ))
                        : <span className="no-label">No labels</span>}
                    </span>
                    <span className="timestamp">{draft.timeStamp}</span>
                  </button>

                  {draft.isRead ? (
                    <button
                      className="mark-unread-button"
                      title="Mark as Unread"
                      onClick={() => markAsUnreadByType(draft.id, token, drafts, setDrafts)}
                    >
                      ✉️
                    </button>
                  ) : (
                    <button
                      className="mark-read-button"
                      title="Mark as Read"
                      onClick={() => markAsReadByType(draft.id, token, drafts, setDrafts)}
                    >
                      📩
                    </button>
                  )}

                  <button
                    className="trash-button"
                    title="Delete"
                    onClick={() => deleteItemByType(draft.id, token, drafts, setDrafts)}
                  >
                    🗑️
                  </button>

                  {selectedForLabels === draft.id ? (
                    <LabelSelectorButton
                      mailId={draft.id}
                      currentLabels={draft.labels}
                      token={token}
                      onClose={() => setSelectedForLabels(null)}
                    />
                  ) : (
                    <button
                      className="labels-button"
                      title="Labels"
                      onClick={() => setSelectedForLabels(draft.id)}
                    >
                      🏷️
                    </button>
                  )}

                  <button
                    className="spam-button"
                    title="Mark as Spam"
                    onClick={() => markAsSpamByType(draft.id, token, drafts, setDrafts)}
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

export default Drafts;