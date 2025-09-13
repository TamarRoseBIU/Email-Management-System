const express = require("express");
const router = express.Router();
const blacklist = require("../controllers/blacklistController");
const authMiddleware = require("../utils/authUtil");

// Matches POST /api/blacklist
// Matches DELETE /api/blacklist/:id
router
  .route("/")
  .post(authMiddleware, blacklist.addToBlacklist)
  .get(authMiddleware, blacklist.getUrls);

router.route("/:id").delete(authMiddleware, blacklist.removeFromBlacklist);

module.exports = router;
