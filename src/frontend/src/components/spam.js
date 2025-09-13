import React, { useState, useEffect, useContext } from "react";
import "./MailsView.css";
import { AuthContext } from "../context/AuthContext";
import LabelSelectorButton from "./LabelSelectorButton";
import StarToggleButton from "./StarToggleButton";
import {
  markAsReadByType,
  markAsUnreadByType,
  deleteItemByType,
  markAsSpamByType,
  toggleStarByType,
} from "./ButtonsByType";

const Spam = () => {
  const [selectedEmail, setSelectedEmail] = useState(null);
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedForLabels, setSelectedForLabels] = useState(null);
  const { user } = useContext(AuthContext);

  useEffect(() => {
    const port = process.env.REACT_APP_BACKEND_PORT;
    const API_URL = `http://localhost:${port}/api/spam`;

    if (!user?.token) {
      setError("User not logged in or token missing.");
      setLoading(false);
      return;
    }

    const fetchSpamEmails = async () => {
      try {
        const response = await fetch(API_URL, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${user.token}`,
          },
        });

        if (!response.ok) throw new Error(`Failed to fetch spam emails.`);

        const data = await response.json();
        setEmails(data);
        setLoading(false);
      } catch (err) {
        setError(err.message);
        setLoading(false);
      }
    };

    fetchSpamEmails();
    const intervalId = setInterval(fetchSpamEmails, 2000);
    return () => clearInterval(intervalId);
  }, [user]);

  const handleRestore = async (id) => {
    const port = process.env.REACT_APP_BACKEND_PORT;
    try {
      await fetch(`http://localhost:${port}/api/spam/restore/${id}`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      setEmails((prev) => prev.filter((email) => email.id !== id));
    } catch (err) {
      alert(`Failed to restore mail: ${err.message}`);
    }
  };

  // const handleDelete = async (id) => {
  //   const port = localStorage.getItem("api-port") || "8080";
  //   try {
  //     await fetch(`http://localhost:${port}/api/trash/from-spam/${id}`, {
  //       method: "POST",
  //       headers: {
  //         Authorization: `Bearer ${user.token}`,
  //       },
  //     });
  //     setEmails((prev) => prev.filter((email) => email.id !== id));
  //   } catch (err) {
  //     alert(`Failed to delete mail: ${err.message}`);
  //   }
  // };

  const handleDelete = async (id) => {
    const port = process.env.REACT_APP_BACKEND_PORT;
    try {
      await fetch(`http://localhost:${port}/api/spam/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      setEmails((prev) => prev.filter((email) => email.id !== id));
    } catch (err) {
      alert(`Failed to delete mail: ${err.message}`);
    }
  };

  if (selectedEmail) {
    return (
      <div className="email-detail-container">
        <button className="back-button" onClick={() => setSelectedEmail(null)}>
          ← Back to Spam
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
  const handleMarkAsUnread = (emailId) => {
    //const userId =   localStorage.getItem("user-id") || "b4beafa7-f120-4faf-b52d-10bcf1550cf4";
    const port = process.env.REACT_APP_BACKEND_PORT;

    fetch(`http://localhost:${port}/api/mails/unread/${emailId}`, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${user.token}`, // Use token from AuthContext
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Failed to mark email as unread.`);
        const updatedEmails = emails.map((email) =>
          email.id === emailId ? { ...email, isRead: false } : email
        );
        setEmails(updatedEmails);
      })
      .catch((err) => {
        alert(`Failed to mark as unread: ${err.message}`);
      });
  };

  const handleMarkAsRead = (emailId) => {
    // const userId =
    //  localStorage.getItem("user-id") || "b4beafa7-f120-4faf-b52d-10bcf1550cf4";
    const port = process.env.REACT_APP_BACKEND_PORT;

    fetch(`http://localhost:${port}/api/mails/read/${emailId}`, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${user.token}`, // Use token from AuthContext
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Failed to mark email as read.`);
        const updatedEmails = emails.map((email) =>
          email.id === emailId ? { ...email, isRead: true } : email
        );
        setEmails(updatedEmails);
      })
      .catch((err) => {
        alert(`Failed to mark as read: ${err.message}`);
      });
  };

  return (
    <div className="inbox-container">
      <h2>🕵️ Spam</h2>
      {loading && <p>Loading spam emails...</p>}
      {error && <p className="error">{error}</p>}
      {!loading && !error && emails.length === 0 && (
        <div className="empty-spam-message">📭 No spam emails to show.</div>
      )}
      {!loading && !error && (
        <ul className="email-list">
          {emails.map((email) => (
            <li key={email.id}>
              <div className="email-item-wrapper">
                <button
                  className="star-button"
                  title="Star"
                  onClick={() =>
                    toggleStarByType(
                      email.id,
                      email.isStarred,
                      user.token,
                      emails,
                      setEmails
                    )
                  }
                >
                  {email.isStarred ? "★" : "☆"}
                </button>
                <button
                  className={`email-item ${email.isRead ? "" : "unread"}`}
                  onClick={() => setSelectedEmail(email)}
                >
                  <span className="sender">{email.from}</span>
                  <div className="subject-fixed-wrapper">
                    {(email.source === "drafts") && <span className="draft-absolute">Draft</span>}
                    <span className="subject"><strong>{email.subject || "(No Subject)"}</strong></span>
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
                      markAsUnreadByType(
                        email.id,
                        user.token,
                        emails,
                        setEmails
                      )
                    }
                  >
                    ✉️
                  </button>
                ) : (
                  <button
                    className="mark-read-button"
                    title="Mark as Read"
                    onClick={() =>
                      markAsReadByType(email.id, user.token, emails, setEmails)
                    }
                  >
                    📩
                  </button>
                )}
                <button
                  className="trash-button"
                  title="Delete permanently"
                  // onClick={() => handleDelete(email.id)}
                  onClick={() =>
                    deleteItemByType(email.id, user.token, emails, setEmails)
                  }
                >
                  🗑️
                </button>

                {selectedForLabels === email.id ? (
                  <LabelSelectorButton
                    mailId={email.id}
                    currentLabels={email.labels}
                    token={user.token}
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
                  className="restore-button"
                  title="Restore from Spam"
                  onClick={() => handleRestore(email.id)}
                >
                  ↩️
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Spam;
