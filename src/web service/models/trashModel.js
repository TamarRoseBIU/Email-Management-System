const mongoose = require("mongoose");
const { v4: uuidv4 } = require("uuid");

const trashSchema = new mongoose.Schema({
  id:       { type: String, required: true, index: true },  
  owner:    { type: String, required: true, index: true },  
  from:     { type: String, required: true },
  to:       { type: [String], default: [] },
  subject:  { type: String, default: "" },
  body:     { type: String, default: "" },
  labels:   { type: [String], default: [] },
  timeStamp:{ type: Date, default: Date.now },
  mailType: { type: String, enum: ["sent", "received", "sent and received"], default: "sent" },
  isRead:   { type: Boolean, default: false },
  isStarred:{ type: Boolean, default: false },
  trashedAt: { type: Date, default: Date.now },
  trashSource: { type: String, enum: ["inbox", "drafts", "spam"], required: true },
});

trashSchema.index({ id:1, owner:1 }, { unique: true });

module.exports = mongoose.model("Trash", trashSchema);
