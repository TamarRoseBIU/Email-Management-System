const express = require("express");
const router = express.Router();
const mailsQueryController = require("../controllers/mailsQueryController");
const authMiddleware = require("../utils/authUtil");

router.route("/").get(authMiddleware, mailsQueryController.getLastSearches);
router.route("/").delete(authMiddleware, mailsQueryController.clearHistory);
router.delete("/:query", authMiddleware, mailsQueryController.deleteSingleQuery);

module.exports = router;
