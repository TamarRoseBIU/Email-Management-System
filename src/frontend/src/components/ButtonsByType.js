export const determineMailType = async (id, token) => {
  const port = process.env.REACT_APP_BACKEND_PORT;

  try {
    const response = await fetch(`http://localhost:${port}/api/objects/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      return null;
    }

    const data = await response.json();
    return data.message || null;
  } catch (err) {
    return null;
  }
};


export const deleteItemByType = async (emailId, token, emails, setEmails) => {
  const type = await determineMailType(emailId, token);

  if (!type) {
    alert("Object not found in mails, drafts, trash or spam.");
    return;
  }

  const port = process.env.REACT_APP_BACKEND_PORT;

  try {
    let response;
    if (type === "spam") {
      response = await fetch(
        `http://localhost:${port}/api/trash/from-spam/${emailId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } else if (type === "drafts") {
      response = await fetch(
        `http://localhost:${port}/api/trash/from-draft/${emailId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } else if (type === "mails") {
      response = await fetch(
        `http://localhost:${port}/api/trash/from-inbox/${emailId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } else if (type === "trash") {
      response = await fetch(`http://localhost:${port}/api/trash/${emailId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    if (!response.ok) {
      throw new Error(`Failed to delete ${type}.`);
    }

    setEmails(emails.filter((email) => email.id !== emailId));
  } catch (err) {
    alert(`Delete failed: ${err.message}`);
  }
};

export const saveLabelsByType = async (mailId, labels, token) => {
  const type = await determineMailType(mailId, token);

  if (!type) {
    alert("Object not found in mails, drafts, trash or spam.");
    return;
  }

  const port = process.env.REACT_APP_BACKEND_PORT;

  const response = await fetch(
    `http://localhost:${port}/api/${type}/label/${mailId}`,
    {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ labels }),
    }
  );

  if (!response.ok) {
    throw new Error(`Failed to update labels in ${type}.`);
  }
};

export const markAsReadByType = async (emailId, token, emails, setEmails) => {
  const type = await determineMailType(emailId, token);

  if (!type) {
    alert("Object not found in mails, drafts, trash or spam.");
    return;
  }

  const port = process.env.REACT_APP_BACKEND_PORT;

  const res = await fetch(
    `http://localhost:${port}/api/${type}/read/${emailId}`,
    {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) throw new Error(`Failed to mark as read in ${type}.`);

  const updatedEmails = emails.map((email) =>
    email.id === emailId ? { ...email, isRead: true } : email
  );
  setEmails(updatedEmails);
};

export const markAsUnreadByType = async (emailId, token, emails, setEmails) => {
  const type = await determineMailType(emailId, token);

  if (!type) {
    alert("Object not found in mails, drafts, trash or spam.");
    return;
  }

  const port = process.env.REACT_APP_BACKEND_PORT;

  const res = await fetch(
    `http://localhost:${port}/api/${type}/unread/${emailId}`,
    {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) throw new Error(`Failed to mark as unread in ${type}.`);

  const updatedEmails = emails.map((email) =>
    email.id === emailId ? { ...email, isRead: false } : email
  );
  setEmails(updatedEmails);
};

export const markAsSpamByType = async (emailId, token, emails, setEmails) => {
  const type = await determineMailType(emailId, token);

  if (!type || type === "spam") {
    alert("Cannot mark this item as spam.");
    return;
  }

  const port = process.env.REACT_APP_BACKEND_PORT;

  try {
    const response = await fetch(
      `http://localhost:${port}/api/spam/from-${type}/${emailId}`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      throw new Error(`Failed to mark as spam from ${type}`);
    }

    setEmails(emails.filter((email) => email.id !== emailId));
  } catch (err) {
    alert(`Failed to mark as spam: ${err.message}`);
  }
};

export const toggleStarByType = async (
  emailId,
  isStarred,
  token,
  emails,
  setEmails
) => {
  const port = process.env.REACT_APP_BACKEND_PORT;

  const type = await determineMailType(emailId, token);

  if (!type) {
    alert("Object not found in mails, drafts, trash or spam.");
    return;
  }

  const endpoint = isStarred
    ? `http://localhost:${port}/api/${type}/unstar/${emailId}`
    : `http://localhost:${port}/api/${type}/star/${emailId}`;

  try {
    const response = await fetch(endpoint, {
      method: "PATCH",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error(`Failed to ${isStarred ? "unstar" : "star"} the mail.`);
    }

    const updatedEmails = emails.map((email) =>
      email.id === emailId ? { ...email, isStarred: !isStarred } : email
    );

    setEmails(updatedEmails);
  } catch (err) {
    alert(`Failed to ${isStarred ? "unstar" : "star"} mail: ${err.message}`);
  }
};
