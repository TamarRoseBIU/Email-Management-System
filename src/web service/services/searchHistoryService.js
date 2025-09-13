const SearchHistory = require("../models/searchHistoryModel");

// Save a search query
async function saveSearchQuery(userId, query) {
  const searchEntry = new SearchHistory({
    userId,
    query,
    timestamp: new Date()
  });

  await searchEntry.save();
}

// Get last unique search queries (max `limit`)
async function getLastSearchQueries(userId, limit = 6) {
  const all = await SearchHistory.find({ userId }).sort({ timestamp: -1 });

  const seen = new Set();
  const unique = [];

  for (const entry of all) {
    if (!seen.has(entry.query)) {
      seen.add(entry.query);
      unique.push(entry.query);
    }
    if (unique.length === limit) break;
  }

  return unique;
}

// Delete all search queries for a user
async function deleteSearchQueriesByUserId(userId) {
  await SearchHistory.deleteMany({ userId });
}

// Delete one specific query for a user
async function deleteOneSearchQuery(userId, query) {
  await SearchHistory.deleteOne({ userId, query });
}

module.exports = {
  saveSearchQuery,
  getLastSearchQueries,
  deleteSearchQueriesByUserId,
  deleteOneSearchQuery
};
