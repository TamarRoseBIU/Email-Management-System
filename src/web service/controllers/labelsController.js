const labelService = require("../services/labelsService");
const { getUserById } = require("../services/userService");

exports.getLabels = async (req, res) => {
  const userId = req.userId;
  const { limit } = req.query;

  if (!userId) return res.status(401).json({ error: "User ID is required" });
  const userById = await getUserById(userId);
  if (!userById)
    return res.status(404).json({ error: "User not found" });

  try {
    const labels = await labelService.getLabels(userId, limit);
    res.json(labels);
  } catch (e) {
    res.status(500).json({ error: "Failed to fetch labels", detail: e.message });
  }
};

exports.getLabelById = async (req, res) => {
  const userId = req.userId;
  const id = req.params.id;

  if (!userId) return res.status(401).json({ error: "User ID is required" });
  const userById = await getUserById(userId);
  if (!userById)
    return res.status(404).json({ error: "User not found" });

  try {
    const label = await labelService.getLabelById(id);
    if (!label) return res.status(404).json({ error: "Label not found" });
    if (label.userId !== userId)
      return res.status(403).json({ error: "Unauthorized" });

    res.json(label);
  } catch (e) {
    res.status(500).json({ error: "Failed to fetch label", detail: e.message });
  }
};

exports.addLabel = async (req, res) => {
  const userId = req.userId;
  const { name } = req.body;

  if (!userId) return res.status(401).json({ error: "User ID is required" });
  const userById = await getUserById(userId);
  if (!userById)
    return res.status(404).json({ error: "User not found" });

  if (!name || typeof name !== "string" || name.trim() === "")
    return res.status(400).json({ error: "Valid name is required" });

  try {
    const label = await labelService.addLabel(name.trim(), userId);
    res.status(201).location(`/api/labels/${label._id}`).json(label);
  } catch (e) {
    res.status(409).json({ error: e.message });
  }
};

exports.updateLabel = async (req, res) => {
  const userId = req.userId;
  const { name } = req.body;
  const id = req.params.id;

  if (!userId) return res.status(401).json({ error: "User ID is required" });
  const userById = await getUserById(userId);
  if (!userById)
    return res.status(404).json({ error: "User not found" });
  const username = userById.username;
  if (!name || name.trim() === "")
    return res.status(400).json({ error: "Valid name is required" });

  try {
    const updatedLabel = await labelService.updateLabel(id, name.trim(), userId, username);
    res.status(200).json(updatedLabel);
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};

exports.deleteLabel = async (req, res) => {
  const userId = req.userId;
  const id = req.params.id;

  if (!userId) return res.status(401).json({ error: "User ID is required" });
  const userById = await getUserById(userId);
  if (!userById)
    return res.status(404).json({ error: "User not found" });
  const username = userById.username;

  try {
    await labelService.deleteLabel(id, userId, username);
    res.status(204).json({ deletedId: id, message: "Label deleted" });
  } catch (e) {
    res.status(400).json({ error: e.message });
  }
};
