import React, { useState, useEffect } from "react";
import "./LabelSelectorButton.css";
import { saveLabelsByType } from "./ButtonsByType";

const LabelSelectorButton = ({ mailId, currentLabels, token, onClose }) => {
  const [showModal, setShowModal] = useState(true);
  const [userLabels, setUserLabels] = useState([]);
  const [selectedLabels, setSelectedLabels] = useState(currentLabels || []);

  const port = process.env.REACT_APP_BACKEND_PORT;
  const API_BASE = `http://localhost:${port}`;

  useEffect(() => {
    fetchUserLabels();
  }, []);

  const fetchUserLabels = async () => {
    try {
      const res = await fetch(`${API_BASE}/api/labels`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error("Failed to fetch labels");
      const data = await res.json();
      setUserLabels(data.labels || data || []);
    } catch (err) {
      console.error("Failed to fetch labels", err);
    }
  };

  const toggleLabel = (labelName) => {
    setSelectedLabels((prev) =>
      prev.includes(labelName)
        ? prev.filter((name) => name !== labelName)
        : [...prev, labelName]
    );
  };

  const saveLabels = async () => {
    try {
      await saveLabelsByType(mailId, selectedLabels, token);
      setShowModal(false);
      if (onClose) onClose();
    } catch (err) {
      console.error("Failed to update labels", err);
    }
  };

  const closeModal = () => {
    setShowModal(false);
    if (onClose) onClose();
  };

  if (!showModal) return null;

  return (
    <div className="modal" role="dialog" aria-modal="true">
      <div className="modal-content">
        <h3>Choose Labels:</h3>
        <div className="label-list">
          {userLabels.length === 0 ? (
            <p>There are no labels for display</p>
          ) : (
            userLabels.map((label) => (
              <label key={label.id || label.name}>
                <input
                  type="checkbox"
                  checked={selectedLabels.includes(label.name)}
                  onChange={() => toggleLabel(label.name)}
                />
                {label.name}
              </label>
            ))
          )}
        </div>
        <div className="button-group">
          <button className="save-button" onClick={saveLabels}>
            Save
          </button>
          <button className="cancel-button" onClick={closeModal}>
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default React.memo(LabelSelectorButton);
