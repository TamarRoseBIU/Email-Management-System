const TrashModel = require("../models/trashModel");

// Add a new trash mail
async function addTrashMail(mail, source) {
 const mailData = mail.toObject ? mail.toObject() : mail;
 const { _id, ...mailWithoutId } = mailData;
  const trashedMail = new TrashModel({
    ...mailWithoutId,
    trashedAt: new Date(),
    trashSource: source,
  });

  return await trashedMail.save();
}

// Get trash by ID (and username)
async function getTrashById(id, username) {
  return await TrashModel.findOne({ id: id, owner: username });
}

// Get all trash mails for a user
async function getAllTrashByUser(username) {
  return await TrashModel.find({ owner: username }).sort({ trashedAt: -1 });
}

// Get all trash (admin/debug)
async function getAllTrash() {
  return await TrashModel.find({});
}

// Delete a trash mail by ID
async function deleteTrashById(id, username) {
  const result = await TrashModel.deleteOne({ id: id, owner: username });
  return result.deletedCount > 0;
}

// Final delete (same as above for now)
async function finalDeleteTrashById(id, username) {
  return await deleteTrashById(id, username);
}

// Mark as read
async function markReadTrash(trash) {
  if (!trash) return false;
  trash.isRead = true;
  await trash.save();
  return true;
}

// Mark as unread
async function markUnreadTrash(trash) {
  if (!trash) return false;
  trash.isRead = false;
  await trash.save();
  return true;
}

// Create a copy of trash (used when restoring)
async function createCopyOfTrash(mail) {
  try {
    const copy = new TrashModel({ 
      ...mail.toObject(), 
      _id: undefined,
      trashedAt: new Date(),
      trashSource: "spam"  
    });
    await copy.save();
    return copy;
  } catch (error) {
    return null;
  }
}
// Update labels in trash
async function editLabelsInTrash(id, username, newLabels) {
  const uniqueLabels = [...new Set(newLabels.filter((label) => typeof label === "string"))];

  try {
    const result = await TrashModel.updateOne(
      { id: id, owner: username },
      { $set: { labels: uniqueLabels } }
    );
    return result.modifiedCount > 0;
  } catch (e) {
    return false;
  }
}

module.exports = {
  addTrashMail,
  getTrashById,
  getAllTrashByUser,
  getAllTrash,
  deleteTrashById,
  finalDeleteTrashById,
  createCopyOfTrash,
  markReadTrash,
  markUnreadTrash,
  editLabelsInTrash,
};
