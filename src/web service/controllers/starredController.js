
const { getUserById } = require("../services/userService");
// { searchAllLabelsArray } = require("../models/labelsModel");
//const starredModel = require("../models/starredModel");
const starredService = require("../services/starredService");


exports.getStarredObjects = async (req, res) => {
  const userId = req.userId; // comes from the token
 
  if (!userId) {
    console.log(userId);
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);

  // TO CHECK THIS
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const username = userById.username;


  try {
    // return 50 recent mails of this username
    return res.status(200).json(await starredService.getStarredByUser(username));
  } catch (e) {
    // error in the server
    return res
      .status(500)
      .json({ error: "Failed to fetch starred objects", detail: e.message || String(e) });
  }
};

/*
exports.starredObject = (req, res) => {
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
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const type = starredModel.isDraftOrMail(id, username);

  if (type === "mail") {
        // Check if the mail exists
        const mailExists = mailModel.getMailById(id, username);
        if (!mailExists) {
            return res.status(404).json({ error: "Mail not found for this user" });
        }

        if (!mailModel.markStarredMail(mailExists)) {
            return res.status(500).json({ error: "Failed to mark email as starred" });
        }
  } else if (type === "draft") {
    // Check if the mail exists
        const draftExists = draftModel.getDraftById(id, username);
        if (!draftExists) {
            return res.status(404).json({ error: "Draft not found for this user" });
        }

        if (!draftModel.markStarredDraft(draftExists)) {
            return res.status(500).json({ error: "Failed to mark draft as starred" });
        }
  } else {
        if (type === null || type === undefined) {
            return res.status(404).json({error: "Object (mail/draft) with this id doesn't exists for this user."});
        }
        return res.status(500).json({error: "Error in starred object. could not decide if draft or mail."})
  }
  return res.status(204).send(); // No content to return, just a success status
};

exports.unstarredObject = (req, res) => {
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
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const type = starredModel.isDraftOrMail(id, username);

  if (type === "mail") {
        // Check if the mail exists
        const mailExists = mailModel.getMailById(id, username);
        if (!mailExists) {
            return res.status(404).json({ error: "Mail not found for this user" });
        }

        if (!mailModel.markUnstarredMail(mailExists)) {
            return res.status(500).json({ error: "Failed to mark email as unstarred" });
        }
  } else if (type === "draft") {
    // Check if the mail exists
        const draftExists = draftModel.getDraftById(id, username);
        if (!draftExists) {
            return res.status(404).json({ error: "Draft not found for this user" });
        }

        if (!draftModel.markUnstarredDraft(draftExists)) {
            return res.status(500).json({ error: "Failed to mark draft as unstarred" });
        }
  } else {
        if (type === null || type === undefined) {
            return res.status(404).json({error: "Object (mail/draft) with this id doesn't exists for this user."});
        }
        return res.status(500).json({error: "Error in unstarred object. could not decide if draft or mail."})
  }
  return res.status(204).send(); // No content to return, just a success status
};

*/

/*
exports.deleteObject = (req, res) => {
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
  const userById = getUserById(userId);
  const username = userById ? userById.username : null;
  // Check if the user exists
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  const type = starredModel.isDraftOrMail(id, username);

  if (type === "mail") {
        // Check if the mail exists
        const mailExists = mailModel.getMailById(id, username);
        if (!mailExists) {
          return res.status(404).send("Mail not found for this user");
        }

        const deleted = mailModel.deleteMailById(id, username);
        if (!deleted) {
          return res.status(500).send("Failed to delete mail");
        }
        return res.status(200).send("Mail deleted successfully");
  } else if (type === "draft") {
    // Check if the mail exists
        const draftExists = draftModel.getDraftById(id, username);
        if (!draftExists) {
          return res.status(404).json({ error: "Draft not found for this user" });
        }

        const deleted = draftModel.deleteDraftById(draftId, username);
        if (!deleted) {
          return res.status(500).send("Failed to delete draft");
        }
        return res.status(200).send("Draft deleted successfully");
  } else {
        if (type === null || type === undefined) {
          return res.status(404).json({error: "Object (mail/draft) with this id doesn't exists for this user."});
        }
        return res.status(500).json({error: "Error in deletting object. could not decide if draft or mail."})
  }
};
*/