
const { getUserById } = require("../services/userService");
const { saveSearchQuery } = require("../services/searchHistoryService");
const { getLastSearchQueries } = require("../services/searchHistoryService");
const { deleteSearchQueriesByUserId } = require("../services/searchHistoryService");
const { deleteOneSearchQuery } = require("../services/searchHistoryService");
const searchAllsModel = require("../services/searchAllService");

exports.searchMails = async (req, res) => {
  //const userId = req.userId;
  const userId = req.userId;
  const saveSearch = req.headers["save-search"] === "true";
  const { query } = req.params;

  // console.log(`🔍 Query: "${query}"`);
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

exports.getLastSearches = async (req, res) => {
  const userId = req.userId;
  const amount = 6;
  // get last 6 searches for the user
  if (!userId) {
    return res.status(400).json({ error: "User ID is required" });
  }
  const userById = await getUserById(userId);
  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  try {
    const lastSearches = await getLastSearchQueries(userId, amount);
    // res.json(lastSearches);
    return res.status(200).json({
      userId,
      count: lastSearches.length,
      searches: lastSearches
    });
  }
  catch (err) {
    return res.status(500).json({
      error: "Failed to retrieve last searches",
      detail: err.message,
    });
  }
};

exports.clearHistory = async (req, res) => {
  const userId = req.userId;;
  if (!userId) {
    return res.status(400).json({ error: "Missing user-id" });
  }
  const userById = await getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  try {
    await deleteSearchQueriesByUserId(userId);
    res.status(204).send();
  }
  catch (err) {
    return res.status(500).json({
      error: "Failed to clear search history",
      detail: err.message,
    });
  }
};

exports.deleteSingleQuery = async (req, res) => {
  const userId = req.userId;
  const query = decodeURIComponent(req.params.query);

  if (!userId || !query) {
    return res.status(400).json({ error: "Missing userId or query" });
  }

  const userById = await getUserById(userId);

  if (!userById) {
    return res.status(404).json({ error: "User with this user-id not found" });
  }
  try {
    await deleteOneSearchQuery(userId, query);
    res.status(200).json({ message: "Query deleted" });
  }
  catch (err) {
    return res.status(500).json({
      error: "Failed to delete query",
      detail: err.message,
    });
  }
};