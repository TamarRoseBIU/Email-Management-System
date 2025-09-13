const fs = require("fs");
const path = require("path");
const { v4: uuidv4 } = require("uuid");
//let idCounter = 0;
const { sendToBlacklistServer } = require("../utils/socketClientService");
let blacklist = [];

// Load blacklist from file at startup
const loadBlacklistFromFile = () => {
  const filePath = path.join(__dirname, "../../../data/data.txt");
  try {
    const data = fs.readFileSync(filePath, "utf8");
    blacklist = data
      .split("\n")
      .map((url) => url.trim())
      .filter((url) => url)
      .map((url) => ({ id: uuidv4(), url }));
    // console.log(
    //   "✅ Blacklist loaded from file with",
    //   blacklist.length,
    //   "entries."
    // );
  } catch (err) {
    console.error("Failed to load blacklist file:", err.message);
  }
};

const getUrls = () => {
  return blacklist;
};

const addToBlacklist = async (url) => {
  const existingEntry = blacklist.find((entry) => entry.url === url);
  if (existingEntry) {
    return existingEntry;
  }
  // CHANGE HERE
  const result = await sendToBlacklistServer("POST", url);
  //const result = "201 Created";
  // only if result is 201 created
  if (result !== "201 Created") {
    // throw an error message like the response from the server
    throw new Error(`${result}`);
    // return null;
  }
  const newURL = { id: uuidv4(), url };
  blacklist.push(newURL);
  return newURL;
};

// helper function to remove URL from blacklist array
const removeFromBlacklistArray = (id) => {
  const lenBefore = blacklist.length;
  blacklist = blacklist.filter((entry) => entry.id !== id);
  return blacklist.length < lenBefore;
};

const removeFromBlacklist = async (id) => {
  const entry = blacklist.find((entry) => entry.id === id);
  if (!entry) {
    return false; // Not found
  }
  // print url to remove
  const result = await sendToBlacklistServer("DELETE", entry.url);

  if (result !== "204 No Content") {
    return false;
  }

  return removeFromBlacklistArray(id);
};
module.exports = {
  getUrls,
  addToBlacklist,
  removeFromBlacklist,
  loadBlacklistFromFile,
};
