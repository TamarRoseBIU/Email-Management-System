const API_BASE = () => {
  const port = process.env.REACT_APP_BACKEND_PORT;
  return `http://localhost:${port}/api`;
};

// fetching drafts
export const fetchDrafts = async (token) => {
  const res = await fetch(`${API_BASE()}/drafts`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Failed to fetch drafts");
  const responseData = await res.json();
  // Extract the data array if the response has the same structure as your search API
  return responseData.data || responseData;
};

// mark as read
export const markDraftAsRead = async (draftId, token) => {
  const res = await fetch(`${API_BASE()}/drafts/read/${draftId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Failed to mark as read");
};

// mark as unread
export const markDraftAsUnread = async (draftId, token) => {
  const res = await fetch(`${API_BASE()}/drafts/unread/${draftId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Failed to mark as unread");
};

// delete draft - FIXED VERSION
export const deleteDraft = async (draftId, token) => {
  const res = await fetch(`${API_BASE()}/trash/from-draft/${draftId}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!res.ok) throw new Error("Failed to delete draft");
  return res; // Return response for success handling
};

// update labels
export const updateLabels = async (emailId, labels, token) => {
  const res = await fetch(`${API_BASE()}/labels/${emailId}`, {
    method: "PATCH",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ labels }),
  });
  if (!res.ok) throw new Error("Failed to update labels");
  return res;
};
