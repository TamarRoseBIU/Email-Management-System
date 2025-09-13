const cron = require("node-cron");
//const trashModel = require("../models/trashModel");
const trashService = require("../services/trashService");

function startTrashCleanup() {
  cron.schedule("0 0 * * *", () => {
    const now = Date.now();
    const thirtyDaysAgo = now - 30 * 24 * 60 * 60 * 1000;
    const allTrash = trashService.getAllTrash();
    allTrash.forEach((mail) => {
      const trashedTime = new Date(mail.trashedAt).getTime();
      if (trashedTime < thirtyDaysAgo) {
        trashService.finalDeleteTrashById(mail.id, mail.owner);
      }
    });
  });
}

module.exports = { startTrashCleanup };
