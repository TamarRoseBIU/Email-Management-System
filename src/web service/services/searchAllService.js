const inboxModel = require("../services/mailService")
const draftsModel = require("../services/draftService");
const labelsModel = require("../services/labelsService");

/**
 * Gather all mailboxes for a user
 * @param {string} username
 * @returns {Promise<Array>} all mails
 */
async function getAllMails(username) {
  const [inbox, drafts] = await Promise.all([
    inboxModel.getRecentMailsOfUser(username),
    draftsModel.getDraftsByUser(username),
  ]);
  const combined = [
    ...inbox.map((m) => ({ ...m.toObject(), source: "inbox" })),
    ...drafts.map((m) => ({ ...m.toObject(), source: "drafts" })),
  ];
  combined.sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
  return combined;
}

exports.filterMailsByLabel = async (labelName, username) => {
  const all = await getAllMails(username);
  const lbl = labelName.toLowerCase();
  return all.filter(
    (m) =>
      Array.isArray(m.labels) && m.labels.some((l) => l.toLowerCase() === lbl)
  );
};

const searchMailContentInsensitive =  (query, newMail) => {
  if (!newMail) return false;
  const q = query.toLowerCase();

  const hasQueryInFields =
    (newMail.subject && newMail.subject.toLowerCase().includes(q)) ||
    (newMail.body && newMail.body.toLowerCase().includes(q)) ||
    (newMail.from && newMail.from.toLowerCase().includes(q));

  const hasQueryInToRecipients =
    newMail.to &&
    Array.isArray(newMail.to) &&
    newMail.to.some((recipient) => recipient.toLowerCase().includes(q));

  const labelsObject = labelsModel.convertLabelsArrayToObjects(newMail.labels);
  const hasQueryInLabels =
    labelsObject &&
    Array.isArray(labelsObject) &&
    labelsObject.some(
      (label) => label.name && label.name.toLowerCase().includes(q)
    );

  return hasQueryInFields || hasQueryInToRecipients || hasQueryInLabels;
};

exports.filterAllMailsByQuery = async (query, username) => {
  const allMails = await getAllMails(username);

  return allMails.filter((mail) => {
    return mail.owner === username && searchMailContentInsensitive(query, mail);
  });
};
