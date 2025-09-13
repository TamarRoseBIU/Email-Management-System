
const express = require("express");
const router = express.Router();
const labels = require("../controllers/labelsController");
const authMiddleware = require("../utils/authUtil");

router.route("/")
  .post(authMiddleware, labels.addLabel)
  .get(authMiddleware ,labels.getLabels);

router
  .route("/:id")
  .get(authMiddleware, labels.getLabelById)
  .patch(authMiddleware, labels.updateLabel)
  .delete(authMiddleware, labels.deleteLabel);

module.exports = router;
