const { findUserByUsername, getUserById } = require("../services/userService");
const mailService = require("../services/mailService");
const labelsService = require("../services/labelsService");

exports.writeNewMail = async (req, res) => {
  try {
    const { subject, body } = req.body;
    let { to = [] } = req.body;
    const labels = [];
    const userId = req.userId; // token middleware

    if (!Array.isArray(to)) to = [to];

    if (!userId) {
      return res.status(401).json({ error: "Valid user-id is required" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }

    const from = userById.username;

    if (to.length === 0) {
      return res.status(400).json({ error: "Missing required fields or empty 'to' field" });
    }

    const invalidRecipients = [];
    for (const username of to) {
      const userExists = await findUserByUsername(username);
      if (!userExists) invalidRecipients.push(username);
    }

    if (invalidRecipients.length > 0) {
      return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
    }

    const senderExists = await findUserByUsername(from);
    if (!senderExists) {
      return res.status(404).json({ error: "Sender doesn't exist" });
    }

    if (from !== userById.username) {
      return res.status(403).json({
        error: "You are not authorized to send emails as this user. Change the 'from' field.",
      });
    }

    const newMail = await mailService.createMail({ from, to, subject, body, labels, mailId: null });

    if (!newMail) {
      return res.status(200).json({ message: "Have blacklisted URLs", warning: "Created in spam." });
    }

    return res.status(201).json({ message: "Email sent successfully", mail: newMail });
  } catch (error) {
    console.error("writeNewMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.deleteMail = async (req, res) => {
  try {
    const mailId = req.params.id;
    const userId = req.userId;

    if (!userId) {
      return res.status(400).json({ error: "user-id required in header" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mail = await mailService.getMailById(mailId, username);
    if (!mail) {
      return res.status(404).send("Mail not found for this user");
    }

    const deleted = await mailService.deleteMailById(mailId, username);
    if (!deleted) {
      return res.status(500).send("Failed to delete mail");
    }
    return res.status(200).send("Mail deleted successfully");
  } catch (error) {
    console.error("deleteMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.getInboxMailsOfUser = async (req, res) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "Valid user-id is required" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mails = await mailService.getInboxMailsOfUser(username);
    return res.status(200).json(mails);
  } catch (e) {
    console.error("getInboxMailsOfUser error:", e);
    return res.status(500).json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};

exports.getSentMails = async (req, res) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "Valid user-id is required" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mails = await mailService.getSentMailsOfUser(username);
    return res.status(200).json(mails);
  } catch (e) {
    console.error("getSentMails error:", e);
    return res.status(500).json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};

exports.getRecentMailsOfUser = async (req, res) => {
  try {
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "Valid user-id is required" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const { range, labelName } = req.query;

    let mails = await mailService.getInboxMailsOfUser(username);

    if (labelName) {
      mails = mails.filter(mail => mail.labels && mail.labels.includes(labelName));
    }

    if (range) {
      const rangeMatch = range.match(/^(\d+)(?:-(\d+))?$/);
      if (!rangeMatch) {
        return res.status(400).json({
          error: "Invalid range format. Use 'N' or 'N-M'",
        });
      }

      const start = parseInt(rangeMatch[1]);
      const end = rangeMatch[2] ? parseInt(rangeMatch[2]) : start;

      if (rangeMatch[2]) {
        const startIndex = Math.max(0, start - 1);
        const endIndex = Math.min(mails.length, end);
        mails = mails.slice(startIndex, endIndex);
      } else {
        mails = mails.slice(-start);
      }
    }

    return res.status(200).json(mails);
  } catch (e) {
    console.error("getRecentMailsOfUser error:", e);
    return res.status(500).json({ error: "Failed to fetch mails", detail: e.message || String(e) });
  }
};

exports.getMailById = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "User id required in header" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mail = await mailService.getMailById(id, username);
    if (!mail) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    return res.status(200).json(mail);
  } catch (error) {
    console.error("getMailById error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.readMail = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }
    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mailExists = await mailService.getMailById(id, username);
    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const result = await mailService.markReadMail(mailExists);
    if (!result) {
      return res.status(500).json({ error: "Failed to mark email as read" });
    }

    return res.status(204).send();
  } catch (error) {
    console.error("readMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.unreadMail = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }
    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mailExists = await mailService.getMailById(id, username);
    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const result = await mailService.markUnreadMail(mailExists);
    if (!result) {
      return res.status(500).json({ error: "Failed to mark email as unread" });
    }

    return res.status(204).send();
  } catch (error) {
    console.error("unreadMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.editMail = async (req, res) => {
  try {
    const { id } = req.params;
    const { subject, body, labels = [] } = req.body;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }
    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mailExists = await mailService.getMailById(id, username);
    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const hasBlacklistedURL = mailService.searchMailWithChangesForBlacklistedURLs(subject, body, labels, mailExists);
    if (hasBlacklistedURL) {
      return res.status(200).json({ message: "Have blacklisted URLs", warning: "Moved to spam." });
    }

    const updated = await mailService.editMail(id, username, { subject, body, labels });
    if (!updated) {
      return res.status(500).json({ error: "Failed to update email" });
    }

    return res.status(200).json({ message: "Email updated successfully" });
  } catch (error) {
    console.error("editMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.addLabelToMail = async (req, res) => {
  try {
    const { mailId } = req.params;
    const { labelId } = req.body;
    const userId = req.userId;

    if (!userId) return res.status(401).json({ error: "user-id required in header" });
    if (!mailId) return res.status(400).json({ error: "Missing required fields - id of mail" });
    if (!labelId) return res.status(400).json({ error: "Missing required fields - id of label" });

    const label = await labelsService.getLabelById(labelId);
    if (!label) return res.status(404).json({ error: "Label not found" });

    const user = await getUserById(userId);
    if (!user) return res.status(404).json({ error: "User with this user-id not found" });
    const username = user.username;

    const mail = await mailService.getMailById(mailId, username);
    if (!mail) return res.status(404).json({ error: "Mail not found" });

    if (mail.labels.includes(label.name)) {
      return res.status(400).json({ error: "Mail already has this label" });
    }

    mail.labels.push(label.name);
    await mailService.editLabelsInMail(mailId, username, mail.labels);

    return res.status(200).json({ message: "Label added to mail" });
  } catch (error) {
    console.error("addLabelToMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.removeLabelFromMail = async (req, res) => {
  try {
    const { mailId, labelName } = req.params;
    const userId = req.userId;

    if (!userId) return res.status(401).json({ error: "user-id required in header" });
    if (!mailId) return res.status(400).json({ error: "Missing required fields - id of mail" });
    if (!labelName) return res.status(400).json({ error: "Missing required fields - name of label" });

    const user = await getUserById(userId);
    if (!user) return res.status(404).json({ error: "User with this user-id not found" });
    const username = user.username;

    const mail = await mailService.getMailById(mailId, username);
    if (!mail) return res.status(404).json({ error: "Mail not found" });

    if (!mail.labels.includes(labelName)) {
      return res.status(400).json({ error: "Mail does not have this label" });
    }

    const newLabels = mail.labels.filter((l) => l !== labelName);
    await mailService.editLabelsInMail(mailId, username, newLabels);

    return res.status(200).json({ message: "Label removed from mail" });
  } catch (error) {
    console.error("removeLabelFromMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.updateLabelsInMail = async (req, res) => {
  try {
    const { id } = req.params;
    const { labels = [] } = req.body;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }
    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }
    const username = userById.username;

    const mailExists = await mailService.getMailById(id, username);
    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const uniqueLabels = [...new Set(labels.filter((l) => typeof l === "string"))];

    const updated = await mailService.editMail(id, username, {
      subject: mailExists.subject,
      body: mailExists.body,
      labels: uniqueLabels,
    });

    if (!updated) {
      return res.status(500).json({ error: "Failed to update labels in mail." });
    }

    return res.status(200).json({ message: "Labels updated successfully" });
  } catch (error) {
    console.error("updateLabelsInMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.starredMail = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }

    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }

    const username = userById.username;
    const mailExists = await mailService.getMailById(id, username);

    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const success = await mailService.markStarredMail(mailExists);
    if (!success) {
      return res.status(500).json({ error: "Failed to mark mail as starred" });
    }

    return res.status(204).send();
  } catch (error) {
    console.error("starredMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};

exports.unstarredMail = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    if (!userId) {
      return res.status(401).json({ error: "user-id required in header" });
    }

    if (!id) {
      return res.status(400).json({ error: "Missing required fields - id of mail" });
    }

    const userById = await getUserById(userId);
    if (!userById) {
      return res.status(404).json({ error: "User with this user-id not found" });
    }

    const username = userById.username;
    const mailExists = await mailService.getMailById(id, username);

    if (!mailExists) {
      return res.status(404).json({ error: "Mail not found for this user" });
    }

    const success = await mailService.markUnstarredMail(mailExists);
    if (!success) {
      return res.status(500).json({ error: "Failed to mark mail as unstarred" });
    }

    return res.status(204).send();
  } catch (error) {
    console.error("unstarredMail error:", error);
    return res.status(500).json({ error: "Internal server error" });
  }
};
