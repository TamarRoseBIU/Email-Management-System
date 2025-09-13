const mailsModel = require("../models/mailsModel");
const draftsModel = require("../models/draftsModel");

// mails object - in memory - we dont save between runs
const starred = [];

const getAllStarred = () => starred;


function getStarredByUser(username) {

    const userMails = mailsModel.getMailsByUser(username);
    const userDrafts = draftsModel.getDraftsByUser(username);


    const allUserObjects = [...userMails, ...userDrafts];

  // filter by username
  const filterByUser = allUserObjects.filter(
    (mail) =>
      mail.owner === username && mail.isStarred === true
  );
  const sortedMails = filterByUser.sort(
    (m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp)
  );
  return sortedMails;
}


function isDraftOrMail(id, username) {
    const userMails = mailsModel.getMailsByUser(username);
    const userDrafts = draftsModel.getDraftsByUser(username);

    const foundInMails = userMails.find(mail => mail.id === id);
    if (foundInMails) return "mail";

    const foundInDrafts = userDrafts.find(draft => draft.id === id);
    if (foundInDrafts) return "draft";

    return null;
}


module.exports = {
  getAllStarred,
  getStarredByUser,
  isDraftOrMail,
};
