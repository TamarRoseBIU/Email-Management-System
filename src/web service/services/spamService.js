// === spamService.js ===
const SpamModel = require("../models/spamModel");
const blacklistModel = require("../services/blacklistService");

function isUrlValid(url) {
  const pattern = /^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$/;
  return pattern.test(url);
}

function extractLinksFromFields(fields) {
  const linksSet = new Set();
  for (const field of fields) {
    const tokens = field.split(/\s+/);
    for (const token of tokens) {
      if (isUrlValid(token)) linksSet.add(token);
    }
  }
  return [...linksSet];
}

async function addSpamMailFromNew(mail) {
  const recipients = Array.isArray(mail.to) ? mail.to : [mail.to];
  const mailId = mail.id;
  const isSelfSend = recipients.includes(mail.from);
  const labels = mail.labels && Array.isArray(mail.labels)
    ? [...new Set(mail.labels.filter((label) => typeof label === "string"))]
    : [];

  const spamCopies = [];

  if (isSelfSend) {
    const selfMail = new SpamModel({
      id: mailId,
      from: mail.from,
      to: recipients,
      subject: mail.subject,
      body: mail.body,
      labels,
      timeStamp: mail.timeStamp,
      mailType: "sent and received",
      owner: mail.from,
      isRead: false,
      isStarred: mail.isStarred,
      source: "inbox",
    });
    await selfMail.save();
    spamCopies.push(selfMail);
  } else {
    const sentMail = {
      id: mailId,
      from: mail.from,
      to: recipients,
      subject: mail.subject,
      body: mail.body,
      labels,
      timeStamp: mail.timeStamp,
      mailType: "sent",
      owner: mail.from,
      isRead: false,
      isStarred: mail.isStarred,
      source: "inbox",
    };
    const mailsService = require("../services/mailService");
    await mailsService.createCopyOfMailForSpam(sentMail);
  }

    for (const recipient of recipients) {
      if (recipient !== mail.from) {
        const receivedMail = new SpamModel({
          id: mailId,
          from: mail.from,
          to: recipients,
          subject: mail.subject,
          body: mail.body,
          labels,
          timeStamp: mail.timeStamp,
          mailType: "received",
          owner: recipient,
          isRead: false,
          isStarred: mail.isStarred,
          source: "inbox",
        });
        await receivedMail.save();
        spamCopies.push(receivedMail);
      }
  }

  return spamCopies;
}

async function createCopyOfSpam(spam) {
  const copy = new SpamModel({ ...spam.toObject(), _id: undefined });
  await copy.save();
  return copy;
}

async function addSpamMailAndBlacklistLinks(mail, source) {
  const spamMail = new SpamModel({ ...mail.toObject(), _id: undefined, source });
  const fields = [mail.subject, mail.body, ...(mail.labels || [])];
  const links = extractLinksFromFields(fields);
  for (const link of links) {
    await blacklistModel.addToBlacklist(link);
  }
  await spamMail.save();
  return true;
}

async function getSpamById(id, username) {
  return await SpamModel.findOne({ id, owner: username });
}

async function getAllSpamByUser(username) {
  return await SpamModel.find({ owner: username }).sort({ timeStamp: -1 });
}

async function deleteSpamById(id, username) {
  const mail = await SpamModel.findOne({ id, owner: username });
  if (!mail) return false;
  const fields = [mail.subject, mail.body, ...(mail.labels || [])];
  const links = extractLinksFromFields(fields);
  const blacklist = blacklistModel.getUrls();
  for (const url of blacklist) {
    const mailsService = require("../services/mailService");
    if (mailsService.searchMailContentInsensitive(url.url, mail)) {
      await blacklistModel.removeFromBlacklist(url.id);
    }
  }
  await SpamModel.deleteOne({ id, owner: username });
  return true;
}

async function finalDeleteSpamById(id, username) {
  return (await SpamModel.deleteOne({ id, owner: username })).deletedCount > 0;
}

async function markReadSpam(spam) {
  if (!spam) return false;
  spam.isRead = true;
  await spam.save();
  return true;
}

async function markUnreadSpam(spam) {
  if (!spam) return false;
  spam.isRead = false;
  await spam.save();
  return true;
}

async function editLabelsInSpam(id, username, newLabels) {
  const uniqueLabels = [...new Set(newLabels.filter((label) => typeof label === "string"))];
  const result = await SpamModel.updateOne({ id, owner: username }, { $set: { labels: uniqueLabels } });
  return result.modifiedCount > 0;
}

async function markStarredSpam(spam) {
  spam.isStarred = true;
  await spam.save();
  return true;
}

async function markUnstarredSpam(spam) {
  spam.isStarred = false;
  await spam.save();
  return true;
}

module.exports = {
  addSpamMailFromNew,
  createCopyOfSpam,
  getSpamById,
  getAllSpamByUser,
  deleteSpamById,
  addSpamMailAndBlacklistLinks,
  finalDeleteSpamById,
  markReadSpam,
  markUnreadSpam,
  editLabelsInSpam,
  markStarredSpam,
  markUnstarredSpam,
};
