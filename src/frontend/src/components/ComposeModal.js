import React, { useState, useRef, useContext } from "react";
import "./ComposeModal.css";
import confetti from "canvas-confetti";
import { AuthContext } from "../context/AuthContext";
import { translateErrorMessage } from "./utils/errorUtils";

const ComposeModal = ({
  onClose,
  initialTo = "",
  initialSubject = "",
  initialBody = "",
  initialLabels = [],
  draftId = null,
}) => {
  const [to, setTo] = useState(initialTo);
  const [subject, setSubject] = useState(initialSubject);
  const [body, setBody] = useState(initialBody);
  const [labels, setLabels] = useState(initialLabels.join(", "));
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [messageType, setMessageType] = useState(null);
  const { user, token } = useContext(AuthContext);
  const [minimized, setMinimized] = useState(false);

  const hasSentRef = useRef(false);

  const [minimizedPosition, setMinimizedPosition] = useState({
    left: 20,
    top: window.innerHeight - 60,
  });

  const modalRef = useRef(null);
  const offset = useRef({ x: 0, y: 0 });
  const isDragging = useRef(false);

  const handleMouseDown = (e) => {
    isDragging.current = true;
    const rect = modalRef.current.getBoundingClientRect();
    offset.current = {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top,
    };
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  const handleMouseMove = (e) => {
    if (!isDragging.current) return;
    const newLeft = e.clientX - offset.current.x;
    const newTop = e.clientY - offset.current.y;
    modalRef.current.style.left = `${newLeft}px`;
    modalRef.current.style.top = `${newTop}px`;

    if (minimized) {
      setMinimizedPosition({ left: newLeft, top: newTop });
    }
  };

  const handleMouseUp = () => {
    isDragging.current = false;
    document.removeEventListener("mousemove", handleMouseMove);
    document.removeEventListener("mouseup", handleMouseUp);
  };

  const minimize = (e) => {
    e.stopPropagation();
    setMinimized(true);
    setMinimizedPosition({
      left: 20,
      top: window.innerHeight - 60,
    });
  };

  const restore = (e) => {
    e.stopPropagation();
    setMinimized(false);
  };

  const showConfetti = () => {
    confetti({
      particleCount: 150,
      spread: 180,
      origin: { y: 0.6 },
    });
  };

  const handleSend = async () => {
    if (hasSentRef.current || loading) return;
    hasSentRef.current = true;
    await submitEmail(false);
  };

  const handleSaveAsDraftAndClose = async () => {
    await submitEmail(true);
  };

  const submitEmail = async (isDraft) => {
    setLoading(true);
    setMessage(null);
    setMessageType(null);

    if (!token && !user?.token) {
      setMessage("User not logged in or token missing.");
      setMessageType("error");
      setLoading(false);
      hasSentRef.current = false;
      return;
    }

    const mailData = {
      to: to.split(",").map((email) => email.trim()),
      subject,
      body,
      labels: labels
        .split(",")
        .map((label) => label.trim())
        .filter((label) => label !== ""),
      draft: isDraft,
    };

    try {
      const port = process.env.REACT_APP_BACKEND_PORT;

      if (isDraft) {
        const url = draftId
          ? `http://localhost:${port}/api/drafts/${draftId}`
          : `http://localhost:${port}/api/drafts`;
        const method = draftId ? "PATCH" : "POST";

        const response = await fetch(url, {
          method,
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
          body: JSON.stringify(mailData),
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error("Draft save error response:", errorText);
          throw new Error(`Failed to save draft: ${response.status}, ${errorText}`);
        }

        let data = {};
        try {
          data = await response.json();
        } catch (err) {
          console.warn("Failed to parse draft JSON response:", err);
        }

        if (data.warning) {
          setMessage(data.message || "Draft moved to spam due blacklist urls.");
          setMessageType("warning");
        } else {
          setMessage(data.message || "Saved as Draft");
          setMessageType("success");
          //showConfetti();
        }

        setTimeout(() => onClose(), 1000);
        return;
      } else {
        const response = await fetch(`http://localhost:${port}/api/mails`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${user.token}`,
          },
          body: JSON.stringify(mailData),
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error("Mail send error response:", errorText);
          throw new Error(`Failed to send email: ${response.status}, ${errorText}`);
        }

        let data = {};
        try {
          data = await response.json();
        } catch (err) {
          console.warn("Failed to parse mail JSON response:", err);
        }

        if (draftId) {
          await fetch(`http://localhost:${port}/api/drafts/${draftId}`, {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${user.token}`,
            },
          });
        }

        if (data.warning) {
          setMessage(data.message || "Mail moved to spam due blacklist urls.");
          setMessageType("warning");
        } else {
          setMessage(data.message || "Email sent successfully!");
          setMessageType("success");
          showConfetti();
        }

        setTimeout(() => onClose(), 1000);
      }
    } catch (err) {
      const { text, styleType } = translateErrorMessage(err.message);
      setMessage(text);
      setMessageType(styleType || "error");
      hasSentRef.current = false;
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {!minimized && (
        <div className="compose-overlay">
          <div className="compose-modal" ref={modalRef} style={{ top: 100, left: 100 }}>
            <div className="compose-header" onMouseDown={handleMouseDown}>
              <span>{subject.trim() !== "" ? subject : "New Mail"}</span>
              <div className="header-buttons">
                <button className="back-btn" onClick={handleSaveAsDraftAndClose} title="Save draft & return">
                  ←
                </button>
                <button className="minimize-btn" onClick={minimize} title="Minimize">
                  &#8211;
                </button>
                <button className="close-btn" onClick={onClose}>
                  ×
                </button>
              </div>
            </div>

            <input type="text" placeholder="To" value={to} onChange={(e) => setTo(e.target.value)} />
            <input type="text" placeholder="Subject" value={subject} onChange={(e) => setSubject(e.target.value)} />
            <textarea placeholder="Body" value={body} onChange={(e) => setBody(e.target.value)} />

            {message && (
              <div
                className={
                  messageType === "success"
                    ? "success-msg"
                    : messageType === "warning"
                    ? "warning-msg"
                    : "error-msg"
                }
              >
                {message}
              </div>
            )}

            <button className="send-btn" onClick={handleSend} disabled={loading || hasSentRef.current}>
              {loading ? "Sending..." : "Send"}
            </button>
          </div>
        </div>
      )}

      {minimized && (
        <div
          className="compose-modal minimized"
          ref={modalRef}
          style={{
            top: minimizedPosition.top,
            left: minimizedPosition.left,
            position: "absolute",
          }}
          onMouseDown={handleMouseDown}
        >
          <div className="compose-header">
            <span>{subject.trim() !== "" ? subject : "New Message"}</span>
            <div className="header-buttons">
              <button className="minimize-btn" onClick={restore} title="Restore">
                &#9633;
              </button>
              <button className="close-btn" onClick={onClose}>
                ×
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default ComposeModal;
