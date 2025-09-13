const express = require("express");
const router = express.Router();
const searchAllController = require("../controllers/searchAllController");
const authMiddleware = require("../utils/authUtil");
//const checkUserId = require("../utils/checkUserId");

//router.use(checkUserId);
router
  .route("/query/:query")
  .get(authMiddleware, searchAllController.searchQuery);
router
  .route("/label/:labelName")
  .get(authMiddleware, searchAllController.searchLabel);
module.exports = router;
