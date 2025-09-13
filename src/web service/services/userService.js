const User = require("../models/usersModel");

// Get all users
const getAllUsers = async () => {
  return await User.find({});
};

// Get user by ID
const getUserById = async (id) => {
  return await User.findById(id);
};

// Find user by username
const findUserByUsername = async (username) => {
  return await User.findOne({ username });
};

// Check user by username and password (no hashing for now)
const checkUserByUsernameAndPassword = async (username, password) => {
  return await User.findOne({ username, password });
};

// Create a new user
const createUser = async (userData) => {
  const user = new User(userData);
  return await user.save();
};

module.exports = {
  getAllUsers,
  getUserById,
  findUserByUsername,
  checkUserByUsernameAndPassword,
  createUser,
};
