// === spamModel.js ===
const mongoose = require("mongoose");

const spamSchema = new mongoose.Schema({
  id:         { type: String, required: true, index: true },
  from:       { type: String, required: true },
  to:         { type: [String], default: [] },
  subject:    { type: String, default: "" },
  body:       { type: String, default: "" },
  labels:     { type: [String], default: [] },
  timeStamp:  { type: Date, default: Date.now },
  mailType:   { type: String, enum: ["sent", "received", "sent and received"], default: "sent" },
  owner:      { type: String, required: true, index: true },
  isRead:     { type: Boolean, default: false },
  isStarred:  { type: Boolean, default: false },
  source:     { type: String, enum: ["inbox", "drafts", "spam", "trash"], default: "inbox" },
});

spamSchema.index({ id: 1, owner: 1 }, { unique: true });

module.exports = mongoose.model("Spam", spamSchema);
