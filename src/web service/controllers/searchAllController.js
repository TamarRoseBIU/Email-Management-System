// searchAllController.js
const searchAllsModel = require("../services/searchAllService");
// const queryMailsModel = require("../models/mailsModel");
const { getUserById } = require("../services/userService");
const { saveSearchQuery } = require('../services/searchHistoryService');
const { getLastSearchQueries } = require("../services/searchHistoryService");
const { deleteSearchQueriesByUserId } = require("../services/searchHistoryService");
const { deleteOneSearchQuery } = require("../services/searchHistoryService");
const {filterAllMailsByQuery} = require("../services/searchHistoryService");

// Search by free-text query
exports.searchQuery = async (req, res) => {
  //const userId = req.userId;
  const userId = req.userId;
  const saveSearch = req.headers["save-search"] === "true";
  const { query } = req.params;

  // console.log(`💾 saveSearch boolean: ${saveSearch}`);
  // console.log(`📋 All headers:`, req.headers);

  if (!query) {
    return res.status(400).json({ error: "Query parameter is required" });
  }
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const userById = await getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }

  try {
    const mails = await searchAllsModel.filterAllMailsByQuery(
      query,
      userById.username
    );
    // Save the search query to the history
  if (saveSearch) {
      await saveSearchQuery(userId, query);
    }
    return res.status(200).json({
      message: "Mails retrieved successfully",
      data: mails,
    });
  } catch (err) {
    return res.status(500).json({
      error: "Failed to search mails",
      detail: err.message,
    });
  }
};

// Search by label name
exports.searchLabel = async (req, res) => {
  const userId = req.userId;
  const { labelName } = req.params;

  if (!labelName) {
    return res.status(400).json({ error: "Label name is required" });
  }
  if (!userId) {
    return res.status(401).json({ error: "Valid user-id is required" });
  }

  const user = await getUserById(userId);
  if (!user) {
    return res.status(404).json({ error: "User not found" });
  }

  try {
    const mails = await searchAllsModel.filterMailsByLabel(
      labelName,
      user.username
    );
    res
      .status(200)
      .json({ message: "Mails retrieved successfully", data: mails });
  } catch (err) {
    res
      .status(500)
      .json({ error: "Failed to search mails by label", detail: err.message });
  }
};
