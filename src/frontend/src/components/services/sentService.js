const API_BASE = () => {
  const port = process.env.REACT_APP_BACKEND_PORT;
  return `http://localhost:${port}/api`;
};


// delete email
export const deleteEmail = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/mails/${emailId}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to delete email");
};

// mark as read
export const markAsRead = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/mails/read/${emailId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to mark as read");
};

// mark as unread
export const markAsUnread = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/mails/unread/${emailId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to mark as unread");
};

// update labels for an email
export const updateLabels = async (emailId, newLabels, token) => {
  const res = await fetch(`${API_BASE()}/mails/labels/${emailId}`, {
    method: "PATCH",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ labels: newLabels }),
  });

  if (!res.ok) throw new Error("Failed to update labels");
};

// mark as spam
export const markAsSpam = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/spam/from-inbox/${emailId}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Failed to mark as spam");
};


