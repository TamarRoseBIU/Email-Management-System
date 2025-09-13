const express = require("express");
const router = express.Router();
const trashController = require("../controllers/trashController");
const authMiddleware = require("../utils/authUtil");

router.route("/").get(authMiddleware, trashController.getTrashMails);

router
  .route("/:id")
  .get(authMiddleware, trashController.getTrashMailById)
  .delete(authMiddleware, trashController.deleteTrashMailById); // Final delete

router
  .route("/restore/:id")
  .post(authMiddleware, trashController.restoreTrashMail);

router
  .route("/from-inbox/:id")
  .post(authMiddleware, trashController.addToTrashFromInbox);

router
  .route("/from-draft/:id")
  .post(authMiddleware, trashController.addToTrashFromDraft);

router
  .route("/from-spam/:id")
  .post(authMiddleware, trashController.addToTrashFromSpam);

router.patch("/read/:id", authMiddleware, trashController.readTrash);
router.patch("/unread/:id", authMiddleware, trashController.unreadTrash);

router
  .route("/label/:id")
  .patch(authMiddleware, trashController.updateLabelsInTrash);

module.exports = router;
