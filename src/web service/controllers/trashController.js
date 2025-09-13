const trashService = require("../services/trashService");
const { getUserById } = require("../services/userService");
const mailService = require("../services/mailService");
const draftService = require("../services/draftService");
const spamService = require("../services/spamService");

// Get all trash mails of the user
exports.getTrashMails = async (req, res) => {
  const userId = req.userId;
  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  try {
    return res.status(200).json(await trashService.getAllTrashByUser(username));
  } catch (e) {
    return res
      .status(500)
      .json({ error: "Failed to fetch trash", detail: e.message || String(e) });
  }
};

// Get one trash mail by ID
exports.getTrashMailById = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await trashService.getTrashById(id, username);

  if (!mail) {
    return res
      .status(404)
      .json({ error: "Trash mail not found for this user" });
  }

  return res.status(200).json(mail);
};

// Delete a trash mail permanently
exports.deleteTrashMailById = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const success = await trashService.finalDeleteTrashById(id, username);

  if (!success) {
    return res
      .status(404)
      .json({ error: "Trash mail not found or already deleted" });
  }

  return res.status(200).json({ message: "Trash mail deleted" });
};

// Restore a trash mail to source
exports.restoreTrashMail = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await trashService.getTrashById(id, username);

  if (!mail) {
    return res
      .status(404)
      .json({ error: "Trash mail not found for this user" });
  }

  let copiedMail;
  let destination;

  // Determine destination and create copy
  switch (mail.trashSource) {
    case "inbox":
      copiedMail = await mailService.createCopyOfMail(mail, username);
      destination = "inbox";
      break;
    case "drafts":
      copiedMail = await draftService.createCopyOfDraft(mail, username);
      destination = "drafts";
      break;
    case "spam":
      copiedMail = await spamService.createCopyOfSpam(mail, username);
      destination = "spam";
      break;
    default:
      copiedMail = await mailService.createCopyOfMail(mail, username);
      destination = "inbox";
  }

  if (!copiedMail) {
    return res
      .status(500)
      .json({ error: `Failed to restore trash mail to ${destination}` });
  }

  const success = await trashService.deleteTrashById(id, username);
  if (!success) {
    return res.status(500).json({ error: "Failed to remove trash mail" });
  }

  return res
    .status(201)
    .json({ message: `Mail restored to ${destination}`, mail: copiedMail });
};

// Add a mail from inbox to trash
exports.addToTrashFromInbox = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await mailService.getMailById(id, username);

  if (!mail) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  try {
    const result = await mailService.deleteMailById(id, username);
    if (!result) {
      return res
        .status(500)
        .json({ error: "Failed to delete mail from inbox" });
    }
  } catch (error) {
    return res.status(500).json({
      error: "Failed to delete mail from inbox",
      detail: error.message || String(error),
    });
  }

  try {
    await trashService.addTrashMail(mail, "inbox");
    return res
      .status(201)
      .json({ message: "Mail marked as trash", mail: mail });
  } catch (error) {
    return res.status(500).json({
      error: "Failed to add mail to trash",
      detail: error.message || String(error),
    });
  }
};

// Add a mail from drafts to trash
exports.addToTrashFromDraft = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await draftService.getDraftById(id, username);
  // console.log("draft:", mail);


  if (!mail) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }
  mail.mailType = "sent"; // Ensure mailType is set to "sent" for drafts

  try {
    // console.log("try delete draft");
    const result = await draftService.deleteDraftById(id, username);
    if (!result) {
      console.log("result is false");
      return res
        .status(500)
        .json({ error: "Failed to delete mail from drafts" });
    }
  } catch (error) {
    // console.log("error in delete draft", error);
    return res.status(500).json({
      error: "Failed to delete mail from drafts",
      detail: error.message || String(error),
    });
  }

  try {
    await trashService.addTrashMail(mail, "drafts");
    return res
      .status(201)
      .json({ message: "Mail marked as trash", mail: mail });
  } catch (error) {
    // console.log("error in add to trash", error);
    return res.status(500).json({
      error: "Failed to add mail to trash",
      detail: error.message || String(error),
    });
  }
};

// Add a mail from spam to trash
exports.addToTrashFromSpam = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await spamService.getSpamById(id, username);

  if (!mail) {
    return res.status(404).json({ error: "Spam not found for this user" });
  }

  try {
    const result = await spamService.finalDeleteSpamById(id, username);
    if (!result) {
      return res.status(500).json({ error: "Failed to delete mail from spam" });
    }
  } catch (error) {
    return res.status(500).json({
      error: "Failed to delete mail from spam",
      detail: error.message || String(error),
    });
  }

  try {
    await trashService.addTrashMail(mail, "spam");
    return res
      .status(201)
      .json({ message: "Mail marked as trash", mail: mail });
  } catch (error) {
    return res.status(500).json({
      error: "Failed to add mail to trash",
      detail: error.message || String(error),
    });
  }
};

exports.readTrash = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of trash email" });
  }

  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const trashMail = await trashService.getTrashById(id, username);
  if (!trashMail) {
    return res
      .status(404)
      .json({ error: "Trash email not found for this user" });
  }

  if (!( await trashService .markReadTrash(trashMail))) {
    return res
      .status(500)
      .json({ error: "Failed to mark trash email as read" });
  }

  return res.status(204).send();
};

exports.unreadTrash = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId;

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of trash email" });
  }

  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const trashMail = await trashService.getTrashById(id, username);
  if (!trashMail) {
    return res
      .status(404)
      .json({ error: "Trash email not found for this user" });
  }

  if (!( await trashService.markUnreadTrash(trashMail))) {
    return res
      .status(500)
      .json({ error: "Failed to mark trash email as unread" });
  }

  return res.status(204).send();
};

exports.updateLabelsInTrash = async (req, res) => {
  const { id } = req.params;
  const { labels = [] } = req.body;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of trash" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;

  const trashExists = await trashService.getTrashById(id, username);
  if (!trashExists) {
    return res.status(404).json({ error: "Trash not found for this user" });
  }

  const updated = await trashService.editLabelsInTrash(id, username, uniqueLabels);

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in trash." });
  }

  return res.status(204).send();
};
