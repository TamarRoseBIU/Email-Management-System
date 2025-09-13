export const translateErrorMessage = (rawMessage) => {
  if (!rawMessage) return { text: "An unknown error occurred.", styleType: "error" };

  const msg = rawMessage.toLowerCase();

  if (msg.includes("blacklist")) {
    return {
      text: "The email contains blacklisted URLs and was saved to Spam.",
      styleType: "warning"
    };
  }

  if (msg.includes("valid user-id is required") || msg.includes("unauthorized")) {
    return { text: "Authentication failed. Please log in again.", styleType: "error" };
  }

  if (msg.includes("user with this user-id not found")) {
    return { text: "User not found.", styleType: "error" };
  }

  if (msg.includes("missing required fields") || msg.includes("'to' field")) {
    return { text: "The 'To' field is required.", styleType: "error" };
  }

  if (msg.includes("some recipients do not exist")) {
    return { text: "Some recipients do not exist. Check the usernames.", styleType: "error" };
  }

  if (msg.includes("sender doesn't exist")) {
    return { text: "Sender does not exist.", styleType: "error" };
  }

  if (msg.includes("you are not authorized to send emails")) {
    return { text: "You are not authorized to send emails as this user.", styleType: "error" };
  }

  if (msg.includes("failed to create email") || msg.includes("500")) {
    return { text: "Server error while sending the email.", styleType: "error" };
  }

  if (msg.includes("failed to save draft")) {
    return { text: "Failed to save draft. Please try again.", styleType: "error" };
  }

  if (msg.includes("network")) {
    return { text: "No internet connection. Please check your network.", styleType: "error" };
  }

  return { text: `Error: ${rawMessage}`, styleType: "error" };
};
