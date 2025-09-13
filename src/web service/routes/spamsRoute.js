const express = require("express");
const router = express.Router();
const spamController = require("../controllers/spamController");
const authMiddleware = require("../utils/authUtil");

// Get all spam mails of the user
// GET /api/spam
router.route("/")
  .get(authMiddleware, spamController.getSpamMails);

// Get one spam mail by ID
// GET /api/spam/:id
router.route("/:id")
  .get(authMiddleware, spamController.getSpamMailById)
  .delete(authMiddleware, spamController.deleteSpamMailById); // Final delete

// Restore a spam mail to inbox
// POST /api/spam/restore/:id
router.route("/restore/:id")
  .post(authMiddleware, spamController.restoreSpamMail);

// Move a mail from inbox to spam
// POST /api/spam/from-inbox/:id
router.route("/from-inbox/:id")
  .post(authMiddleware, spamController.addToSpamFromInbox);

router.route("/from-mails/:id")
  .post(authMiddleware, spamController.addToSpamFromInbox);

router.route("/from-drafts/:id")
  .post(authMiddleware, spamController.addToSpamFromDraft);

router.route("/from-trash/:id")
  .post(authMiddleware, spamController.addToSpamFromTrash);

router.route('/read/:id')
  .patch(authMiddleware, spamController.readSpam);

router.route('/unread/:id')
  .patch(authMiddleware, spamController.unreadSpam);

router
  .route("/label/:id")
  .patch(authMiddleware, spamController.updateLabelsInSpam);

router
  .route("/star/:id")
  .patch(authMiddleware, spamController.starredSpam);

router
  .route("/unstar/:id")
  .patch(authMiddleware, spamController.unstarredSpam);
module.exports = router;
