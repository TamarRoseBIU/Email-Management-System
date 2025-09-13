const mongoose = require("mongoose");

const userSchema = new mongoose.Schema({
  firstName: { type: String, required: true },
  lastName:  { type: String, required: true },
  username:  { type: String, required: true, unique: true },
  password:  { type: String, required: true },
  phoneNumber: { type: String, required: true },
  birthDate: { type: Date, required: true },
  gender: { type: String, enum: ["male", "female", "other"], required: true },
  profilePic: { type: String },
});

module.exports = mongoose.model("User", userSchema);