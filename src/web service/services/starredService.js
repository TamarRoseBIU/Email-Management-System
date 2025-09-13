
const mailService = require("./mailService");
const draftService = require("./draftService");
const { checkUsernameExists } = require("../controllers/usersController");
//const { use } = require("react");

// mails object - in memory - we dont save between runs
const starred = [];

const getAllStarred = () => starred;

/*
async function getStarredByUser(username) {

    const userMails = await mailService.getMailsByUser(username);
    const userDrafts = await draftService.getDraftsByUser(username);


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


async function isDraftOrMail(id, username) {
    const userMails = await mailService.getMailsByUser(username);
    const userDrafts = await draftService.getDraftsByUser(username);

    const foundInMails = userMails.find(mail => mail.id === id);
    if (foundInMails) return "mail";

    const foundInDrafts = userDrafts.find(draft => draft.id === id);
    if (foundInDrafts) return "draft";

    return null;
}

*/


async function getStarredByUser(username) {
 
  const [userMails, userDrafts] = await Promise.all([
    mailService.getStarredMailsByUser(username),
    draftService.getStarredDraftsByUser(username),
  ]);


  const inboxWithSource = userMails.map((mail) => ({
    ...mail,
    source: "inbox",
  }));

  const draftsWithSource = userDrafts.map((draft) => ({
    ...draft,
    source: "draft",
  }));

  const combined = [...userMails, ...userDrafts];

  combined.sort((m1, m2) => new Date(m2.timeStamp) - new Date(m1.timeStamp));

  return combined;
}

async function isDraftOrMail(id, username) {
  const [foundMail, foundDraft] = await Promise.all([
    mailService.getMailById(id, username),
    draftService.getDraftById(id, username),
  ]);

  if (foundMail) return "mail";
  if (foundDraft) return "draft";

  return null;
}

module.exports = {
  getAllStarred,
  getStarredByUser,
  isDraftOrMail,
};
