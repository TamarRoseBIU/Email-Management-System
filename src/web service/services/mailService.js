const { v4: uuidv4 } = require("uuid");
const Mail = require("../models/mailsModel");
const blacklistModel = require("../services/blacklistService");
const labelsModel = require("../services/labelsService");
//const spamModel = require("../models/spamModel");
const spamService = require("../services/spamService");

async function createMail({ from, to, subject, body, labels, mailId }) {
  const timeStamp = new Date();
  const recipients = Array.isArray(to) ? to : [to];
  const uniqueTo = [...new Set(recipients.filter((r) => typeof r === "string"))];
  const isSelfSend = uniqueTo.includes(from);

  if (!mailId) mailId = uuidv4();

  const newMails = [];
 // const selfMail = [];

  if (isSelfSend) {
    newMails.push(
      new Mail({
        id: mailId,
        from,
        to: uniqueTo,
        subject,
        body,
        labels,
        timeStamp,
        mailType: "sent and received",
        owner: from,
        isRead: false,
        isStarred: false,
      })
    );
  } else {
    newMails.push(
      new Mail({
        id: mailId,
        from,
        to: uniqueTo,
        subject,
        body,
        labels,
        timeStamp,
        mailType: "sent",
        owner: from,
        isRead: true,
        isStarred: false,
      })
    );
  }

  for (const recipient of uniqueTo) {
    if (recipient !== from) {
      newMails.push(
        new Mail({
          id: mailId,
          from,
          to: uniqueTo,
          subject,
          body,
          labels,
          timeStamp,
          mailType: "received",
          owner: recipient,
          isRead: false,
          isStarred: false,
        })
      );
    }
  }

  //let isBlacklisted = false;

  for (const mail of newMails) {
    if (searchMailForBlacklistedURLs(mail)) {
      await spamService.addSpamMailFromNew(mail);
     // isBlacklisted = true;
      return null;
    }
  }
  /*
  if (isBlacklisted) {
    if (isSelfSend) {
      selfMail.push(
        new Mail({
        id: mailId,
        from,
        to: uniqueTo,
        subject,
        body,
        labels,
        timeStamp,
        mailType: "sent and received",
        owner: from,
        isRead: false,
        isStarred: false,
      }));
    } else {
      selfMail.push(
        new Mail({
          id: mailId,
          from,
          to: uniqueTo,
          subject,
          body,
          labels,
          timeStamp,
          mailType: "sent",
          owner: from,
          isRead: true,
          isStarred: false,
        })
      );
    }
    await Mail.insertMany(selfMail);
    return null;
  }
  */

  try {
    await Mail.insertMany(newMails);
    return newMails;
  } catch (error) {
    console.error("Failed to create mails:", error);
    return null;
  }
}

async function getInboxMailsOfUser(username) {
  return await Mail.find({
    owner: username,
    mailType: { $in: ["sent and received", "received"] },
  }).sort({ timeStamp: -1 });
}

async function getSentMailsOfUser(username) {
  return await Mail.find({
    owner: username,
    mailType: { $in: ["sent and received", "sent"] },
  }).sort({ timeStamp: -1 });
}

async function getAllMails() {
  return await Mail.find({}).sort({ timeStamp: -1 });
}

async function getMailById(id, username) {
  return await Mail.findOne({ id, owner: username });
}

async function getRecentMailsOfUser(username) {
  return await Mail.find({ owner: username })
    .sort({ timeStamp: -1 })
    .limit(50);
}

async function getMailsByUser(username) {
  return await Mail.find({ owner: username }).sort({ timeStamp: -1 });
}

async function getStarredMailsByUser(username) {
  return await Mail.find({ owner: username, isStarred: true }).sort({ timeStamp: -1 });
}

async function deleteMailById(id, username) {
  const result = await Mail.deleteOne({ id, owner: username });
  return result.deletedCount > 0;
}

async function createCopyOfMail(mail) {
  try {
    const copyMail = new Mail({ ...mail.toObject(), _id: undefined });
    await copyMail.save();
    return copyMail;
  } catch (error) {
    console.error("Failed to create copy of mail:", error);
    return null;
  }
}

async function createCopyOfMailForSpam(mail) {
  try {
    const copyMail = new Mail({ ...mail, _id: undefined });
    await copyMail.save();
    return copyMail;
  } catch (error) {
    console.error("Failed to create copy of mail:", error);
    return null;
  }
}

async function markReadMail(mail) {
  try {
    mail.isRead = true;
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

async function markUnreadMail(mail) {
  try {
    mail.isRead = false;
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

async function editMail(id, username, { subject, body, labels }) {
  const mail = await Mail.findOne({ id, owner: username });
  if (!mail) return false;
  try {
    if (subject !== undefined) mail.subject = subject;
    if (body !== undefined) mail.body = body;
    if (labels !== undefined)
      mail.labels = [...new Set(labels.filter((label) => typeof label === "string"))];
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

async function editLabelsInMail(id, username, newLabels) {
  const mail = await Mail.findOne({ id, owner: username });
  if (!mail) return false;
  try {
    mail.labels = [...new Set(newLabels.filter((label) => typeof label === "string"))];
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

function searchMailContentInsensitive(query, mail) {
  const q = query.toLowerCase();
  return (
    (mail.subject?.toLowerCase().includes(q) ||
      mail.body?.toLowerCase().includes(q) ||
      mail.from?.toLowerCase().includes(q) ||
      mail.to?.some((r) => r.toLowerCase().includes(q)) ||
      labelsModel
        .convertLabelsArrayToObjects(mail.labels)
        ?.some((l) => l.name?.toLowerCase().includes(q))) ?? false
  );
}

function searchMailContentSensitive(query, mail) {
  return (
    (mail.subject?.includes(query) ||
      mail.body?.includes(query) ||
      mail.from?.includes(query) ||
      mail.to?.some((r) => r.includes(query)) ||
      labelsModel.convertLabelsArrayToObjects(mail.labels)?.some((l) => l.name?.includes(query))) ??
    false
  );
}

async function filterMailsByQueryCaseInsensitive(query, username) {
  const mails = await Mail.find({ owner: username });
  return mails.filter((mail) => searchMailContentInsensitive(query, mail));
}

async function filterMailsByQueryCaseSensitive(query, username) {
  const mails = await Mail.find({ owner: username });
  return mails.filter((mail) => searchMailContentSensitive(query, mail));
}

function searchMailForBlacklistedURLs(mail) {
  return blacklistModel.getUrls().some((entry) => searchMailContentInsensitive(entry.url, mail));
}

function searchMailWithChangesForBlacklistedURLs(subject, body, labels, mailExists) {
  const copyChangedMail = {
    ...mailExists,
    subject: subject ?? mailExists.subject,
    body: body ?? mailExists.body,
    labels: labels ?? mailExists.labels,
  };
  return searchMailForBlacklistedURLs(copyChangedMail);
}

async function markStarredMail(mail) {
  try {
    mail.isStarred = true;
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

async function markUnstarredMail(mail) {
  try {
    mail.isStarred = false;
    await mail.save();
    return true;
  } catch {
    return false;
  }
}

module.exports = {
  createMail,
  getInboxMailsOfUser,
  getSentMailsOfUser,
  getAllMails,
  getMailById,
  getRecentMailsOfUser,
  getMailsByUser,
  getStarredMailsByUser,
  markReadMail,
  markUnreadMail,
  editMail,
  editLabelsInMail,
  filterMailsByQueryCaseSensitive,
  filterMailsByQueryInsensitive: filterMailsByQueryCaseInsensitive,
  deleteMailById,
  searchMailForBlacklistedURLs,
  searchMailWithChangesForBlacklistedURLs,
  createCopyOfMail,
  searchMailContentInsensitive,
  searchMailContentSensitive,
  markStarredMail,
  markUnstarredMail,
  createCopyOfMailForSpam,
};
