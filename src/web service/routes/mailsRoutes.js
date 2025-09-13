const express = require("express");
const router = express.Router();
const mailsController = require("../controllers/mailsController");
const authMiddleware = require("../utils/authUtil");
// get recent mails of user
router
  .route("/")
  .get(authMiddleware, mailsController.getInboxMailsOfUser)
  .post(authMiddleware, mailsController.writeNewMail);
// GET /api/mails/:id - we want to get a mail by id

router
  .route("/sent/")
  .get(authMiddleware, mailsController.getSentMails);
  
router
  .route("/:id")
  .get(authMiddleware, mailsController.getMailById)
  .patch(authMiddleware, mailsController.editMail)
  .delete(authMiddleware, mailsController.deleteMail);


router
  .route("/star/:id")
  .patch(authMiddleware, mailsController.starredMail);

router
    .route("/unstar/:id")
    .patch(authMiddleware,mailsController.unstarredMail);


router
  .route("/label/:id")
  .delete(authMiddleware, mailsController.removeLabelFromMail)
  .post(authMiddleware, mailsController.addLabelToMail)
  .patch(authMiddleware, mailsController.updateLabelsInMail);

router.route("/read/:id")
  .patch(authMiddleware, mailsController.readMail);

router.route("/unread/:id")
  .patch(authMiddleware, mailsController.unreadMail);


module.exports = router;
