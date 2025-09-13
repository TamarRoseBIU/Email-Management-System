const express = require("express");
const router = express.Router();
const authMiddleware = require("../utils/authUtil");
const usersController = require("../controllers/usersController");

// POST /tokens (login)
router.post("/tokens", usersController.login);

// POST /users (create user with optional profile picture)
router.post("/users", usersController.createUser);

// GET /user (get user by token)
router.get("/user", authMiddleware, usersController.getUserByToken);

// GET /user/username/:username (check if username exists)
router.get("/user/username/:username", usersController.checkUsernameExists);

// GET /users/:id (get full user info)
router.get("/users/:id", authMiddleware, usersController.getUserDetails);

module.exports = router;
