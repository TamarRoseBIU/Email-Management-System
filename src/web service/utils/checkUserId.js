const checkUserId = (req, res, next) => {
  const userId = req.headers["user-id"] || req.headers["user-id"];
  if (!userId) {
    return res.status(401).json({ error: "user-id header is required" });
  }
  if (typeof userId !== "string" || userId.trim() === "") {
    return res.status(400).json({ error: "Valid user-id is required" });
  }
  req.userId = userId.trim(); // Ensure no extra whitespace
  //console.log("Extracted userId:", req.userId); // Debug log
  next();
};

module.exports = checkUserId;
