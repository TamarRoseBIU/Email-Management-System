import React from "react";

const MarkAsSpamButton = ({ emailId, token, onSuccess }) => {
    const port = process.env.REACT_APP_BACKEND_PORT;

    const handleMarkAsSpam = async () => {
        try {
            await fetch(`http://localhost:${port}/api/spam/from-inbox/${emailId}`, {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (onSuccess) onSuccess(); // עדכן את הרשימה אחרי פעולה מוצלחת
        } catch (err) {
            alert(`Failed to mark mail as spam: ${err.message}`);
        }
    };

    return (
        <button
            className="spam-button"
            title="Mark as Spam"
            onClick={handleMarkAsSpam}
        >
            🚫
        </button>
    );
};

export default MarkAsSpamButton;
