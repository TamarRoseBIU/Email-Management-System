const mongoose = require('mongoose');
const { v4: uuidv4 } = require("uuid");

const draftSchema = new mongoose.Schema({
  id:       { type: String, required: true, index: true },  
  owner:    { type: String, required: true, index: true },  
  from:     { type: String, required: true },
  to:       { type: [String], default: [] },
 subject:  { type: String, default: "" },
  body:     { type: String, default: "" },
  labels:   { type: [String], default: [] },
  timeStamp:{ type: Date, default: Date.now },
  mailType: { type: String, default: "" },
  isRead:   { type: Boolean, default: false },
  isStarred:{ type: Boolean, default: false },
});

draftSchema.index({ id:1, owner:1 }, { unique: true });

module.exports = mongoose.model('Draft', draftSchema);