const express = require("express");
const router = express.Router();
const draftsController = require("../controllers/draftsController");
const mailsQueryController = require("../controllers/mailsQueryController");
const authMiddleware = require("../utils/authUtil");

// get recent mails of user
router.route("/")
  .get(authMiddleware, draftsController.getAllDraftsOfUser)
  .post(authMiddleware, draftsController.writeNewDraft);


router.route('/:id')
    .get(authMiddleware, draftsController.getDraftById)
    .patch(authMiddleware, draftsController.editDraft)
    .post(authMiddleware, draftsController.convertDraftToMail)
    .delete(authMiddleware, draftsController.deleteDraft);

router.route('/read/:id')
    .patch(authMiddleware, draftsController.readDraft);

router.route('/unread/:id')
    .patch(authMiddleware, draftsController.unreadDraft);

router
  .route("/star/:id")
  .patch(authMiddleware, draftsController.starredDraft);

router
    .route("/unstar/:id")
    .patch(authMiddleware,draftsController.unstarredDraft);


router.route("/search/:query")
    .get(authMiddleware, mailsQueryController.searchMails);

router
  .route("/label/:id")
  .patch(authMiddleware, draftsController.updateLabelsInDraft);

module.exports = router;


