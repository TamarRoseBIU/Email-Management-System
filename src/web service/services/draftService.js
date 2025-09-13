const Draft = require("../models/draftsModel");
//const blacklistModel = require("../models/blacklistModel");
const blacklistService = require("../services/blacklistService");
//const labelsModel = require("../models/labelsModel");
const labelsService = require("../services/labelsService");
const { v4: uuidv4 } = require("uuid");

/*
const getAllDrafts = async (username) => {
  return Draft.find({ owner: username }).exec();
};

const getDraftById = async (id, username) => {
  return Draft.findOne({ id, owner: username }).exec();
};
*/

async function getAllDrafts(username) {
  return await Draft.find({ owner: username }).sort({ timeStamp: -1 });
}

async function getDraftById(id, username) {
  return await Draft.findOne({ id, owner: username });
  //return await Draft.findOne({ id });
}


/*
const createDraft = async (username, draftData) => {

  const uniqueTo = Array.isArray(draftData.to)
    ? [...new Set(draftData.to.filter((recipient) => typeof recipient === "string"))]
    : [];

  const uniqueLabels = Array.isArray(draftData.labels)
    ? [...new Set(draftData.labels.filter((label) => typeof label === "string"))]
    : [];

  const newDraft = new Draft({
    id: uuidv4(),
    from: username,
    owner: username,
    to: uniqueTo,
    subject: draftData.subject || "",
    body: draftData.body || "",
    labels: uniqueLabels,
    mailType: draftData.mailType || "",
  });

  return await newDraft.save();
};

*/

/*
async function createDraft({ from, to, subject, body, labels, mailType, draftId }) {
  const timeStamp = new Date();

  const recipients = Array.isArray(to) ? to : [to];
  const uniqueTo = [...new Set(recipients.filter((r) => typeof r === "string"))];

  const uniqueLabels = Array.isArray(labels)
    ? [...new Set(labels.filter((label) => typeof label === "string"))]
    : [];

  const id = draftId || uuidv4();

  const newDraft = new Draft({
    id,
    from,
    owner: from,
    to: uniqueTo,
    subject: subject || "",
    body: body || "",
    labels: uniqueLabels,
    mailType: mailType || "",
    timeStamp,
    isRead: false,
    isStarred: false,
  });

  try {
    const savedDraft = await newDraft.save();
    return savedDraft;
  } catch (error) {
    console.error("Failed to create draft:", error);
    console.log("Failed to create draft:", error);
    return null;
  }
}
*/



/*

async function createDraft({ from, to, subject, body, labels, mailType, draftId }) {
  //from = from || "maayan"; // Default to "maayan" if from is not provided
  console.log("createDraft called with:");
  console.log("from:", from);
  console.log("to:", to);
  console.log("subject:", subject);
  console.log("body:", body);
  console.log("labels:", labels);
  console.log("mailType:", mailType);
  console.log("draftId:", draftId);

  const timeStamp = new Date();

  const recipients = Array.isArray(to) ? to : [to];
  const uniqueTo = [...new Set(recipients.filter((r) => typeof r === "string"))];

  console.log("uniqueTo:", uniqueTo);

  const uniqueLabels = Array.isArray(labels)
    ? [...new Set(labels.filter((label) => typeof label === "string"))]
    : [];

  console.log("uniqueLabels:", uniqueLabels);

  const id = draftId || uuidv4();
  console.log("final draft id:", id);

  const newDraft = new Draft({
    id,
    from: from,
    owner: from,
    to: uniqueTo,
    subject: subject || "",
    body: body || "",
    labels: uniqueLabels,
    mailType: mailType || "",
    timeStamp,
    isRead: false,
    isStarred: false,
  });

  //console.log("newDraft to be saved:", newDraft);

  try {
    const savedDraft = await newDraft.save();
    console.log("Draft saved successfully:", savedDraft);
    return savedDraft;
  } catch (error) {
    console.error("Failed to create draft:", error);
    return null;
  }
}

*/

const createDraft = async ({ from, to, subject, body, labels }) => {
  try {
    const timeStamp = new Date();

    const recipients = Array.isArray(to) ? to : [to];
    const uniqueTo = [
      ...new Set(recipients.filter((r) => typeof r === "string")),
    ];

    const uniqueLabels = Array.isArray(labels)
      ? [...new Set(labels.filter((label) => typeof label === "string"))]
      : [];

    const id = uuidv4();

    const newDraft = new Draft({
      id,
      from,
      owner: from,
      to: uniqueTo,
      subject: subject || "",
      body: body || "",
      labels: uniqueLabels,
      mailType: "",
      timeStamp,
      isRead: false,
      isStarred: false,
    });

    const savedDraft = await newDraft.save();
    return savedDraft;
  } catch (err) {
    console.error("Error creating draft:", err);
    return null;
  }
};


const editDraft = async (id, username, updateData) => {
  if (updateData.to) {
    updateData.to = [...new Set(updateData.to.filter((r) => typeof r === "string"))];
  }
  if (updateData.labels) {
    updateData.labels = [...new Set(updateData.labels.filter((l) => typeof l === "string"))];
  }
  updateData.timeStamp = new Date().toLocaleString("sv-SE", {
    timeZone: "Asia/Jerusalem",
  });
  return Draft.findOneAndUpdate({ id, owner: username }, updateData, { new: true }).exec();
};



const deleteDraftById = async (id, username) => {
  return Draft.findOneAndDelete({ id, owner: username }).exec();
};


const createCopyOfDraft = async (draft) => {
  try {
    if (!draft) return null;
    const draftObj = draft.toObject();
    delete draftObj._id;
    draftObj.timeStamp = new Date().toLocaleString("sv-SE", {
      timeZone: "Asia/Jerusalem",
    });

    const copy = new Draft(draftObj);
    return copy.save();
  } catch (error) {
    return null;
  }
};


/*
const markReadDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isRead: true }, { new: true }).exec();
};



const markUnreadDraft = async (id, username) => {
  return Draft.findOneAndUpdate({ id, owner: username }, { isRead: false }, { new: true }).exec();
};
*/


/*
const markReadDraft = async (id, username) => {
  console.log("📥 [markReadDraft] id:", id, "username:", username);
  try {
    const updated = await Draft.findOneAndUpdate(
      { id, owner: username },
      { isRead: true },
      { new: true }
    ).exec();

    if (!updated) {
      console.log("❌ [markReadDraft] No draft found to update");
    } else {
      console.log("✅ [markReadDraft] Updated draft:", updated);
    }

    return updated;
  } catch (error) {
    console.error("🔥 [markReadDraft] Error:", error);
    throw error;
  }
};*/

async function markReadDraft(draft) {
  try {
    draft.isRead = true;
    await draft.save();
    return true;
  } catch {
    return false;
  }
}


async function markUnreadDraft(draft) {
  try {
    draft.isRead = false;
    await draft.save();
    return true;
  } catch {
    return false;
  }
}

async function markStarredDraft(draft) {
  try {
    draft.isStarred = true;
    await draft.save();
    return true;
  } catch {
    return false;
  }
}

async function markUnstarredDraft(draft) {
  try {
    draft.isStarred = false;
    await draft.save();
    return true;
  } catch {
    return false;
  }
}


const editLabelsInDraft = async (id, username, newLabels) => {
  if (!Array.isArray(newLabels)) return null;
  const uniqueLabels = [...new Set(newLabels.filter((l) => typeof l === "string"))];
  return Draft.findOneAndUpdate({ id, owner: username }, { labels: uniqueLabels }, { new: true }).exec();
};


const filterDraftsByQueryCaseSensitive = async (query, username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.filter((draft) => searchDraftContentSensitive(query, draft));
};


const filterDraftsByQueryCaseInsensitive = async (query, username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.filter((draft) => searchDraftContentInsensitive(query, draft));
};


const searchDraftContentInsensitive = (query, draft) => {
  if (!draft) return false;
  const q = query.toLowerCase();

  const hasQueryInFields =
    (draft.subject && draft.subject.toLowerCase().includes(q)) ||
    (draft.body && draft.body.toLowerCase().includes(q)) ||
    (draft.from && draft.from.toLowerCase().includes(q));

  const hasQueryInToRecipients =
    draft.to && draft.to.some((r) => r.toLowerCase().includes(q));

  const labelsObject = labelsService.convertLabelsArrayToObjects(draft.labels);

  const hasQueryInLabels =
    labelsObject &&
    labelsObject.some((label) => label.name && label.name.toLowerCase().includes(q));

  return hasQueryInFields || hasQueryInToRecipients || hasQueryInLabels;
};


const searchDraftContentSensitive = (query, draft) => {
  if (!draft) return false;

  const hasQueryInFields =
    (draft.subject && draft.subject.includes(query)) ||
    (draft.body && draft.body.includes(query)) ||
    (draft.from && draft.from.includes(query));

  const hasQueryInToRecipients =
    draft.to && draft.to.some((r) => r.includes(query));

  const labelsObject = labelsService.convertLabelsArrayToObjects(draft.labels);

  const hasQueryInLabels =
    labelsObject &&
    labelsObject.some((label) => label.name && label.name.includes(query));

  return hasQueryInFields || hasQueryInToRecipients || hasQueryInLabels;
};


const searchDraftForBlacklistedURLs = (draft) => {
  return blacklistService.getUrls().some((entry) =>
    searchDraftContentInsensitive(entry.url, draft)
  );
};


const searchDraftsForBlacklistedURLs = (mail) => {
  return blacklistService.getUrls().some((entry) =>
    searchDraftContentInsensitive(entry.url, mail)
  );
};


const searchDraftWithChangesForBlacklistedURLs = (subject, body, labels, draftExists) => {
  const finalSubject = subject ?? draftExists.subject;
  const finalBody = body ?? draftExists.body;
  const finalLabels = labels !== undefined ? labels : draftExists.labels;

  const copyChangedDraft = {
    id: draftExists.id,
    from: draftExists.from,
    to: draftExists.to,
    subject: finalSubject,
    body: finalBody,
    labels: finalLabels,
    timeStamp: draftExists.timeStamp,
    draftType: draftExists.draftType,
    owner: draftExists.owner,
    isRead: draftExists.isRead,
    isStarred: draftExists.isStarred,
  };

  return searchDraftForBlacklistedURLs(copyChangedDraft);
};


const getDraftsByUser = async (username) => {
  const drafts = await Draft.find({ owner: username }).exec();
  return drafts.sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
};

const getStarredDraftsByUser = async (username) => {
  const drafts = await Draft.find({ owner: username, isStarred: true }).exec();
  return drafts.sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
};

module.exports = {
  getAllDrafts,
  getDraftById,
  getStarredDraftsByUser,
  createDraft,
  editDraft,
  deleteDraftById,
  createCopyOfDraft,
  markReadDraft,
  markUnreadDraft,
  markStarredDraft,
  markUnstarredDraft,
  editLabelsInDraft,
  filterDraftsByQueryCaseSensitive,
  filterDraftsByQueryCaseInsensitive,
  searchDraftContentInsensitive,
  searchDraftContentSensitive,
  searchDraftForBlacklistedURLs,
  searchDraftWithChangesForBlacklistedURLs,
  searchDraftsForBlacklistedURLs,
  getDraftsByUser,
};