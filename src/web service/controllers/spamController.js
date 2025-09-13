
const spamService = require("../services/spamService");
const { getUserById } = require("../services/userService");
const mailService = require("../services/mailService");
const draftService= require("../services/draftService");
const trashService = require("../services/trashService");
// Get all spam mails of the user
exports.getSpamMails = async (req, res) => {
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
      // return all spam of this username
      return res.status(200).json(await spamService.getAllSpamByUser(username));
    } catch (e) {
      // error in the server
      return res
        .status(500)
        .json({ error: "Failed to fetch spam", detail: e.message || String(e) });
    }
};

// Get one spam mail by ID
exports.getSpamMailById = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  // Validate user ID
  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  // check if user exists
  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;
  const mail = await spamService.getSpamById(id, username);

  if (!mail) {
    return res.status(404).json({ error: "Spam mail not found for this user" });
  }

  return res.status(200).json(mail);
};

// Delete a spam mail 
exports.deleteSpamMailById = async (req, res) => {
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
  const success = await spamService.finalDeleteSpamById(id, username);

  if (!success) {
    return res.status(404).json({ error: "Spam mail not found or already deleted" });
  }

  return res.status(200).json({ message: "Spam mail deleted" });
};

// Restore a spam mail back to inbox
exports.restoreSpamMail = async (req, res) => {
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
    return res.status(404).json({ error: "Spam mail not found for this user" });
  }

  let copiedMail;
  let destination;
  // const copiedMail = mailModel.createCopyOfMail(mail, username);
    switch (mail.source) {
      case "inbox":
        copiedMail = await mailService.createCopyOfMail(mail);
        destination = "inbox";
        break;
      case "drafts":
        copiedMail = await draftService.createCopyOfDraft(mail);
        destination = "drafts";
        break;
      case "trash":
        copiedMail = await trashService.createCopyOfTrash(mail);
        destination = "trash";
        break;
      default:
        copiedMail = await mailService.createCopyOfMail(mail);
        destination = "inbox";
    }

  if (!copiedMail) {
    return res.status(500).json({ error: "Failed to restore spam mail" });
  }
  // remove the mail from spam
  const success = await spamService.deleteSpamById(id, username);
  if (!success) {
    return res.status(500).json({ error: "Failed to remove spam mail" });
  }

  return res.status(201).json({ message: "Mail restored", mail: copiedMail });
};

// Add a mail from inbox to spam
exports.addToSpamFromInbox = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  // Check if user ID is provided
  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;

  // Get the mail from the regular inbox by ID
  const mail = await mailService.getMailById(id, username);

  // Check if the mail exists
  if (!mail) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  try{
    const result = await mailService.deleteMailById(id, username);
    if (!result) {
      return res.status(500).json({ error: "Failed to delete mail from inbox" });
    }
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to delete mail from inbox", detail: error.message || String(error) });
    }
  try{
    // Add the mail to the spam list
  await spamService.addSpamMailAndBlacklistLinks(mail, "inbox");
  return res.status(201).json({ message: "Mail marked as spam", mail: mail });
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to add mail to spam", detail: error.message || String(error) });
    }   
};
// Add a mail from drafts to spam
exports.addToSpamFromDraft = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  // Check if user ID is provided
  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;

  // Get the mail from the regular inbox by ID
  const mail = await draftService.getDraftById(id, username);

  // Check if the mail exists
  if (!mail) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }
  mail.mailType = "sent"

  try{
    const result = await draftService.deleteDraftById(id, username);
    if (!result) {
      return res.status(500).json({ error: "Failed to delete mail from draft" });
    }
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to delete mail from draft", detail: error.message || String(error) });
    }
  try{
    // Add the mail to the spam list
  await spamService.addSpamMailAndBlacklistLinks(mail, "drafts");
  return res.status(201).json({ message: "Mail marked as spam", mail: mail });
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to add mail to spam", detail: error.message || String(error) });
    }   
};

// Add a mail from inbox to spam
exports.addToSpamFromTrash = async (req, res) => {
  const userId = req.userId;
  const { id } = req.params;

  // Check if user ID is provided
  if (!userId) {
    return res.status(401).json({ error: "User ID is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  const username = user.username;

  // Get the mail from the regular inbox by ID
  const mail = await trashService.getTrashById(id, username);

  // Check if the mail exists
  if (!mail) {
    return res.status(404).json({ error: "Mail not found for this user" });
  }

  try{
    const result = await trashService.deleteTrashById(id, username);
    if (!result) {
      return res.status(500).json({ error: "Failed to delete mail from trash" });
    }
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to delete mail from trash", detail: error.message || String(error) });
    }
  try{
    // Add the mail to the spam list
  await spamService.addSpamMailAndBlacklistLinks(mail, "trash");
  return res.status(201).json({ message: "Mail marked as spam", mail: mail });
  }
    catch (error) {
        return res.status(500).json({ error: "Failed to add mail to spam", detail: error.message || String(error) });
    }   
};

// re
exports.readSpam = async (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];
  const userId = req.userId; // comes from the token

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of spam" });
  }
  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the mail exists
  const spamExists = await spamService.getSpamById(id, username);
  if (!spamExists) {
    return res.status(404).json({ error: "Spam not found for this user" });
  }

  if (! (await spamService.markReadSpam(spamExists))) {
    return res.status(500).json({ error: "Failed to mark spam as read" });
  }

  return res.status(204).send(); // No content to return, just a success status
}


exports.unreadSpam= async (req, res) => {
  const { id } = req.params;
  //const userId = req.headers["user-id"];

  const userId = req.userId; // comes from the token

   if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

//if (!userId) {
  //  return res.status(401).json({ error: "user-id required in header" });
  //}
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of spam" });
  }
  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  // Check if the draft exists
  const spamExists = await spamService.getSpamById(id, username);
  if (!spamExists) {
    return res.status(404).json({ error: "Spam not found for this user" });
  }

  if (! (await spamService.markUnreadSpam(spamExists))) {
    return res.status(500).json({ error: "Failed to mark draft as unread" });
  }

  return res.status(204).send(); // No content to return, just a success status
}

exports.updateLabelsInSpam = async (req, res) => {
  const { id } = req.params;
  const { labels = [] } = req.body;
  const userId = req.userId; // from token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }

  if (!id) {
    return res.status(400).json({ error: "Missing required fields - id of spam" });
  }

  const uniqueLabels = [...new Set(labels)];

  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  const username = userById.username;


  const spamExists = await spamService.getSpamById(id, username);
  if (!spamExists) {
    return res.status(404).json({ error: "Spam not found for this user" });
  }


  const updated = await spamService.editLabelsInSpam(
    id,
    username,
    uniqueLabels
  );

  if (!updated) {
    return res.status(500).json({ error: "Failed to update labels in spam." });
  }

  return res.status(204).send(); 
};

exports.unstarredSpam = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
    // Check if the mail exists
  const spamExists = await spamService.getSpamById(id, username);
  if (!spamExists) {
      return res.status(404).json({ error: "Spam not found for this user" });
  }

  if (! (await spamService.markUnstarredSpam(spamExists))) {
      return res.status(500).json({ error: "Failed to mark spam as unstarred" });
  }
  return res.status(204).send(); // No content to return, just a success status
};


exports.starredSpam = async (req, res) => {
  const { id } = req.params;
  const userId = req.userId; // comes from the token

  if (!userId) {
    return res.status(401).json({ error: "user-id required in header" });
  }
  // Check that id of email is not empty
  if (!id) {
    return res
      .status(400)
      .json({ error: "Missing required fields - id of mail" });
  }
  const userById = await getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
    // Check if the mail exists
  const spamExists = await spamService.getSpamById(id, username);
  if (!spamExists) {
      return res.status(404).json({ error: "Spam not found for this user" });
  }

  if (! (await spamService.markStarredSpam(spamExists))) {
      return res.status(500).json({ error: "Failed to mark spam as starred" });
  }
  return res.status(204).send(); // No content to return, just a success status
};


