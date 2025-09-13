const API_BASE = () => {
  const port = process.env.REACT_APP_BACKEND_PORT;
  return `http://localhost:${port}/api/mails`;
};
const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL = `http://localhost:${port}/api`;
// fetching inbox emails
export const fetchInboxEmails = async (token) => {
  const response = await fetch(API_BASE(), {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch emails: ${response.statusText}`);
  }

  return await response.json();
};

// marking an email as read
export const markEmailAsRead = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/read/${emailId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to mark email as read");
};

// marking an email as unread
export const markEmailAsUnread = async (emailId, token) => {
  const res = await fetch(`${API_BASE()}/unread/${emailId}`, {
    method: "PATCH",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to mark email as unread");
};

// deleting an email
export const deleteEmail = async (emailId, token) => {
  /*
      const port = process.env.REACT_APP_BACKEND_PORT;

    fetch(`http://localhost:${port}/api/trash/from-inbox/${emailId}`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) throw new Error(`Failed to delete mail.`);

        const updatedEmails = emails.filter((email) => email.id !== emailId);
        setEmails(updatedEmails);
      })
      .catch((err) => {
        alert(`Failed to delete mail: ${err.message}`);
      });
  */
  const res = await fetch(`${BASE_URL}/trash/from-inbox/${emailId}`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!res.ok) throw new Error("Failed to delete email");

  // fetch(`${BASE_URL}/trash/from-inbox/${emailId}`, {
  //   method: "POST",
  //   headers: {
  //     Authorization: `Bearer ${token}`,
  //   },
  // })
  //   .then((res) => {
  //     if (!res.ok) throw new Error(`Failed to delete mail.`);

  //     // const updatedEmails = emails.filter((email) => email.id !== emailId);
  //     // setEmails(updatedEmails);
  //   })
  //   .catch((err) => {
  //     alert(`Failed to delete mail: ${err.message}`);
  //   });
};

// updating labels for an email
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
};

// // marking an email as spam
// export const markAsSpam = async (emailId, token) => {
//   const port = localStorage.getItem("api-port") || "8080";

//   const res = await fetch(
//     `http://localhost:${port}/api/spam/from-inbox/${emailId}`,
//     {
//       method: "POST",
//       headers: {
//         Authorization: `Bearer ${token}`,
//       },
//     }
//   );

//   if (!res.ok) throw new Error("Failed to mark email as spam");
// };
export const markAsSpam = async (emailId, token) => {
  const port = process.env.REACT_APP_BACKEND_PORT;
  const res = await fetch(
    `http://localhost:${port}/api/spam/from-inbox/${emailId}`,
    {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    }
  );

  if (!res.ok) {
    const text = await res.text();
    console.log("mailId:", emailId);
    console.error("Spam API failed:", res.status, text);
    throw new Error(`Failed to mark email as spam (status ${res.status})`);
  }
};
