const { findUserByUsername } = require("../services/userService");
const { getUserById } = require("../services/userService");
const { searchAllLabelsArray } = require("../services/labelsService");
const draftService = require("../services/draftService");
const mailService = require("../services/mailService");

/*
exports.writeNewDraft = async (req, res) => {
  let { subject, body } = req.body;
  let labels = [];
  let { to = [] } = req.body;
  const userId = req.userId;

  if (!to) to = [];
  if (!Array.isArray(to)) to = [to];

  if (to.length > 0) {
    const invalidRecipients = to.filter((username) => !findUserByUsername(username));
    if (invalidRecipients.length > 0) {
      return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
    }
  }

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const from = userById.username;
  console.log("**************************From:****************************", from);

  if (!findUserByUsername(from)) {
    return res.status(404).json({ error: "Sender dosen't exist" });
  }

  console.log("Draft data:", { from, to, subject, body, labels, mailType: null});
  //const from1 = "maayan";
  try {
    const newDraft = await draftService.createDraft({ from, to, subject, body, labels, mailType: null, draftId: null });

    if (!newDraft) {
      return res.status(500).json({ message: "Failed to create draft" });
    }

    return res.status(201).json({ message: "Draft saved successfully", draft: newDraft });
  } catch (e) {
    return res.status(500).json({ error: e.message || "Failed to create draft" });
  }
};

*/

exports.writeNewDraft = async (req, res) => {
  const { subject, body } = req.body;
  let { to = [] } = req.body;
  const labels = [];

  const userId = req.userId;

  if (!to) to = [];
  if (!Array.isArray(to)) to = [to];

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const from = userById.username;

  if (!findUserByUsername(from)) {
    return res.status(404).json({ error: "Sender doesn't exist" });
  }

  const invalidRecipients = to.filter((username) => !findUserByUsername(username));
  if (invalidRecipients.length > 0) {
    return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
  }

  try {
    const newDraft = await draftService.createDraft({ from, to, subject, body, labels });

    if (!newDraft) {
      return res.status(500).json({ message: "Failed to create draft" });
    }

    return res.status(201).json({ message: "Draft saved successfully", draft: newDraft });
  } catch (e) {
    return res.status(500).json({ error: e.message || "Failed to create draft" });
  }
};

exports.deleteDraft = async (req, res) => {
  const draftId = req.params.id;
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const username = (await getUserById(userId)).username;

  const draft = await draftService.getDraftById(draftId, username);
  if (!draft) {
    return res.status(404).send("Draft not found for this user");
  }

  const deleted = await draftService.deleteDraftById(draftId, username);
  if (!deleted) {
    return res.status(500).send("Failed to delete draft");
  }
  return res.status(200).send("Draft deleted successfully");
};

exports.getAllDraftsOfUser = async (req, res) => {
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;

  try {
    const drafts = await draftService.getAllDrafts(username);
    return res.status(200).json(drafts);
  } catch (e) {
    return res.status(500).json({ error: "Failed to fetch drafts", detail: e.message || String(e) });
  }
};


exports.getDraftById = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;
  //console.log("Username for checking:", username);
  const draft = await draftService.getDraftById(id, username);

  if (!draft) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  return res.status(200).json(draft);
};


exports.readDraft = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;
  const draftExists = await draftService.getDraftById(id, username);

  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  const success = await draftService.markReadDraft(draftExists);
  if (!success) {
    return res.status(500).json({ error: "Failed to mark draft as read" });
  }

  return res.status(204).send();
};

exports.unreadDraft = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;
  const draftExists = await draftService.getDraftById(id, username);

  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  const success = await draftService.markUnreadDraft(draftExists);
  if (!success) {
    return res.status(500).json({ error: "Failed to mark draft as unread" });
  }

  return res.status(204).send();
};


exports.editDraft = async (req, res) => {
  const { id } = req.params;
  let { subject, body, to, labels } = req.body;
  const userId = req.userId;

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;

  const draftExists = await draftService.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  let toArray = to;
  if (!toArray) toArray = [];
  if (!Array.isArray(toArray)) toArray = [toArray];

  if (toArray.length > 0) {
    const invalidRecipients = toArray.filter((username) => !findUserByUsername(username));
    if (invalidRecipients.length > 0) {
      return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
    }
  }

  try {
    const updated = await draftService.editDraft(id, username, { subject, body, to: toArray, labels });
    if (!updated) {
      return res.status(500).json({ error: "Failed to update draft" });
    }
    return res.status(200).json({ message: "Draft updated successfully" });
  } catch (e) {
    return res.status(500).json({ error: e.message || "Failed to update draft" });
  }
};

exports.convertDraftToMail = async (req, res) => {
  const { id } = req.params;
  const draftId = id;
  const userId = req.userId;

  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }
  if (!draftId) {
    return res.status(400).json({ error: "Draft ID is required" });
  }

  const draft = await draftService.getDraftById(draftId);
  if (!draft) {
    return res.status(404).json({ error: "Draft not found" });
  }

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const to = draft.to;
  const from = userById.username;
  const subject = draft.subject;
  const body = draft.body;
  const labels = draft.labels;

  const invalidRecipients = to.filter((username) => !findUserByUsername(username));
  if (invalidRecipients.length > 0) {
    return res.status(400).json({ error: "Some recipients do not exist", invalidRecipients });
  }

  if (!findUserByUsername(from)) {
    return res.status(404).json({ error: "Sender dosen't exist" });
  }

  if (from !== userById.username) {
    return res.status(403).json({
      error: "You are not authorized to send emails as this user. Change the 'from' field.",
    });
  }

  if (!(await searchAllLabelsArray(labels, userId))) {
    return res.status(404).json({ error: "Labels do not exist for this user" });
  }

  const newMail = await mailService.createMail({ from, to, subject, body, labels, draftId });

  if (!newMail) {
    return res.status(200).json({ message: "Have blacklisted URLs", warning: "Created in spam." });
  }

  
  const deleted = await draftService.deleteDraftById(draftId, userById.username);
  if (!deleted) {
    return res.status(500).send("Failed to delete draft");
  }

  
  return res.status(201).json({ message: "Email sent successfully", mail: newMail });
};


exports.updateLabelsInDraft = async (req, res) => {
  const { id } = req.params;
  let { labels = [] } = req.body;
  const userId = req.userId;

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of draft" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;

  const draftExists = await draftService.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  const updated = await draftService.editLabelsInDraft(id, username, uniqueLabels);

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in draft." });
  }

  return res.status(204).send();
};

exports.unstarredDraft = async (req, res) => {
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

  const draftExists = await draftService.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  const success = await draftService.markUnstarredDraft(draftExists);
  if (!success) {
    return res.status(500).json({ error: "Failed to mark draft as unstarred" });
  }

  return res.status(204).send();
};

exports.starredDraft = async (req, res) => {
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

  const draftExists = await draftService.getDraftById(id, username);
  if (!draftExists) {
    return res.status(404).json({ error: "Draft not found for this user" });
  }

  const success = await draftService.markStarredDraft(draftExists);
  if (!success) {
    return res.status(500).json({ error: "Failed to mark draft as starred" });
  }

  return res.status(204).send();
};

exports.getAllDraftsOfUser = async (req, res) => {
  // const username = req.query.username;
  const userId = req.userId; // comes from the token
  // const userId = req.headers["user-id"];

  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;

  try {
    // return all drafts of this username
    const drafts = await draftService.getAllDrafts(username);
    return res.status(200).json(drafts);
  } catch (e) {
    return res.status(500).json({ error: "Failed to fetch drafts", detail: e.message || String(e) });
  }
};
