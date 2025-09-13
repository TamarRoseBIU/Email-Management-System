const express = require("express");
const router = express.Router();
const mailsQueryController = require("../controllers/mailsQueryController");
const authMiddleware = require("../utils/authUtil");
//const checkUserId = require("../utils/checkUserId");

//router.use(checkUserId);
router.route("/:query").get(authMiddleware, mailsQueryController.searchMails);
module.exports = router;
