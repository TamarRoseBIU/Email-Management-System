import React, { useEffect, useState, useContext } from "react";
import { AuthContext } from "../context/AuthContext";
import { useNavigate, useParams } from "react-router-dom";
import "./Labels.css";
import "./MailsView.css";
const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL_LABEL = `http://localhost:${port}/api/labels`;
const BASE_URL = `http://localhost:${port}/api`;

const LabelContainer = ({ onLabelClick }) => {
  const [labels, setLabels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newLabelName, setNewLabelName] = useState("");
  const [editingLabelId, setEditingLabelId] = useState(null);
  const [editLabelName, setEditLabelName] = useState("");
  const [showAll, setShowAll] = useState(false);
  const [allLabels, setAllLabels] = useState([]);

  const { token } = useContext(AuthContext);
  const navigate = useNavigate();
  const { labelName } = useParams();

  // Fetch first 5 labels
  useEffect(() => {
    if (!token) {
      setError("User not logged in or token missing.");
      setLoading(false);
      return;
    }

    (async () => {
      setLoading(true);
      try {
        // fetch *all* labels
        const res = await fetch(BASE_URL_LABEL, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok) throw new Error(`Error ${res.status}`);
        const data = await res.json();
        setAllLabels(data);
        // show only first 5 by default
        setLabels(data.slice(0, 5));
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    })();
  }, [token]);

  const getAllLabels = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${BASE_URL_LABEL}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!response.ok) throw new Error(`Error ${response.status}`);
      const data = await response.json();
      setLabels(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddLabel = async () => {
    if (!newLabelName.trim()) return;

    try {
      const res = await fetch(BASE_URL_LABEL, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name: newLabelName.trim() }),
      });

      if (res.status === 409) {
        setError("Label already exists.");
        return;
      }

      if (!res.ok) throw new Error(`Error ${res.status}`);

      const newLabel = await res.json();

      const updatedAllLabels = [newLabel, ...allLabels];
      setAllLabels(updatedAllLabels);

      if (showAll) {
        setLabels(updatedAllLabels);
      } else {
        setLabels(updatedAllLabels.slice(0, 5));
      }

      setNewLabelName("");
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Error adding label:", err);
    }
  };

  const handleEditLabel = async (label) => {
    if (!editLabelName.trim() || editLabelName.trim() === label.name) {
      setEditingLabelId(null);
      return;
    }

    try {
      const res = await fetch(`${BASE_URL_LABEL}/${label._id}`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name: editLabelName.trim() }),
      });

      if (res.status === 409) {
        setError("Label name already exists.");
        return;
      }

      if (!res.ok) throw new Error(`Error ${res.status}`);

      const updated = await res.json();

      const updatedAllLabels = allLabels.map((l) =>
        l._id === updated._id ? updated : l
      );
      setAllLabels(updatedAllLabels);
      setLabels((prev) => prev.map((l) => (l._id === updated._id ? updated : l)));

      setEditingLabelId(null);
      setError(null);

      if (labelName === label.name) {
        navigate(`/search/label/${encodeURIComponent(updated.name)}`);
      }
    } catch (err) {
      setError(err.message);
      console.error("Error editing label:", err);
    }
  };

  const handleDeleteLabel = async (label, e) => {
    e.stopPropagation();
    try {
      if (!window.confirm(`Delete label "${label.name}"?`)) return;

      const res = await fetch(`${BASE_URL_LABEL}/${label._id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error(`Error ${res.status}`);

      const updatedAllLabels = allLabels.filter((l) => l._id !== label._id);
      setAllLabels(updatedAllLabels);
      setLabels((prev) => prev.filter((l) => l._id !== label._id));

      if (labelName === label.name) {
        navigate("/home");
      }
    } catch (err) {
      setError(err.message);
      console.error("Error deleting label:", err);
    }
  };

  const handleLabelClick = (label) => {
    if (onLabelClick) {
      onLabelClick();
    }
    navigate(`/search/label/${encodeURIComponent(label.name)}`);
  };

  const handleShowAllLabels = () => {
    if (showAll) {
      setLabels(allLabels.slice(0, 5));
      setShowAll(false);
    } else {
      setLabels(allLabels);
      setShowAll(true);
    }
  };

  const handleBackToInbox = () => {
    if (onLabelClick) {
      onLabelClick();
    }
    navigate("/home");
  };

  const handleCancelEdit = (e) => {
    e.stopPropagation();
    setEditingLabelId(null);
    setEditLabelName("");
    setError(null);
  };

  const handleCancelAdd = () => {
    setNewLabelName("");
    setError(null);
  };

  if (loading) return <div>Loading labels...</div>;

  return (
    <div className="label-container">
      <div className="label-header">
        <span>Labels</span>
        <div className="label-header-actions">
          {!newLabelName ? (
            <>
              <button
                className="add-btn"
                onClick={() => setNewLabelName(" ")}
                title="Add new label"
              >
                +
              </button>
              {allLabels.length > 5 && (
                <button
                  className="show-all-btn"
                  onClick={handleShowAllLabels}
                  title={showAll ? "Show less" : "Show all labels"}
                >
                  {showAll ? "⬆" : "..."}
                </button>
              )}
            </>
          ) : null}
        </div>
      </div>

      {newLabelName && (
        <div className="add-label-container">
          <input
            className="label-input"
            value={newLabelName}
            onChange={(e) => setNewLabelName(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleAddLabel()}
            placeholder="New label name"
            autoFocus
          />
          <button onClick={handleAddLabel}>✔</button>
          <button onClick={handleCancelAdd}>✖</button>
        </div>
      )}

      {error && <div className="error">{error}</div>}

      {labelName && (
        <div className="current-filter">
          <button className="back-button" onClick={handleBackToInbox}>
            ← Back to Inbox
          </button>
          <div className="current-label">Viewing: {labelName}</div>
        </div>
      )}

      <ul className={`label-list ${showAll ? "scrollable" : ""}`}>
        {labels.map((label) => (
          <li
            key={label._id}
            className={`label-item ${labelName === label.name ? "active" : ""}`}
            onClick={() => handleLabelClick(label)}
          >
            {editingLabelId === label._id ? (
              <>
                <input
                  className="label-input"
                  value={editLabelName}
                  onChange={(e) => setEditLabelName(e.target.value)}
                  onClick={(e) => e.stopPropagation()}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.stopPropagation();
                      handleEditLabel(label);
                    }
                    if (e.key === "Escape") {
                      e.stopPropagation();
                      handleCancelEdit(e);
                    }
                  }}
                  autoFocus
                />
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleEditLabel(label);
                  }}
                >
                  ✔
                </button>
                <button onClick={handleCancelEdit}>✖</button>
              </>
            ) : (
              <>
                <span className="label-name">{label.name}</span>
                <div className="label-actions">
                  <button
                    className="action-btn edit-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      setEditingLabelId(label._id);
                      setEditLabelName(label.name);
                    }}
                    title="Edit label"
                  >
                    ✏️
                  </button>
                  <button
                    className="action-btn delete-btn"
                    onClick={(e) => handleDeleteLabel(label, e)}
                    title="Delete label"
                  >
                    🗑️
                  </button>
                </div>
              </>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default LabelContainer;
