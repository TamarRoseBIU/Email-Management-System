
const express = require("express");
const router = express.Router();
const objects = require("../controllers/objectsController");
const authMiddleware = require("../utils/authUtil");

router.route("/:id")
  .get(authMiddleware ,objects.determineObjectType);

module.exports = router;
