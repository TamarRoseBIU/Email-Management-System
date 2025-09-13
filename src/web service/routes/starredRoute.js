const express = require("express");
const router = express.Router();
const starredController = require("../controllers/starredController");
const authMiddleware = require("../utils/authUtil");
// get recent mails of user
router
  .route("/")
  .get(authMiddleware, starredController.getStarredObjects);

/*
router
  .route("/:id")
  .delete(authMiddleware,starredController.deleteObject);

router
  .route("/star/:id")
  .patch(authMiddleware, starredController.starredObject);

router
    .route("/unstar/:id")
    .patch(authMiddleware,starredController.unstarredObject)

    */

module.exports = router;
