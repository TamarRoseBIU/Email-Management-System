const Label = require("../models/labelsModel");
const Mail = require("../models/mailsModel");
const Draft = require("../models/draftsModel");
const Spam = require("../models/spamModel");
const Trash = require("../models/trashModel");

const getLabels = async (userId, limit = null) => {
  const query = Label.find({ userId });
  if (limit) query.limit(limit);
  return await query.exec();
};

const getLabelById = async (id) => {
  return await Label.findById(id).exec();
};

const addLabel = async (name, userId) => {
  const exists = await Label.findOne({
    name: { $regex: new RegExp(`^${name}$`, "i") },
    userId,
  }).exec();

  if (exists) throw new Error("Label with this name already exists");

  const newLabel = new Label({ name, userId });
  return await newLabel.save();
};


async function updateLabelInAllElements(oldName, newName, username) {
  try {

    const updateQuery = {
      $set: { "labels.$[elem]": newName },
    };
    const options = {
      arrayFilters: [{ elem: oldName }],
    };

    const mailsUpdate = await Mail.updateMany(
      { owner: username, labels: oldName },
      updateQuery,
      options
    );

    const draftsUpdate = await Draft.updateMany(
      { owner: username, labels: oldName },
      updateQuery,
      options
    );

    const spamUpdate = await Spam.updateMany(
      { owner: username, labels: oldName },
      updateQuery,
      options
    );

    const trashUpdate = await Trash.updateMany(
      { owner: username, labels: oldName },
      updateQuery,
      options
    );

    return { mailsUpdate, draftsUpdate, spamUpdate, trashUpdate };
    //return { mailsUpdate, draftsUpdate };
  } catch (err) {
    console.error("Error updating label in all elements:", err);
    return {};
  }
}

async function deleteLabelInAllElements(nameToRemove, username) {
  try {
    const mailsUpdate = await Mail.updateMany(
      { owner: username, labels: nameToRemove },
      { $pull: { labels: nameToRemove } }
    );

    const draftsUpdate = await Draft.updateMany(
      { owner: username, labels: nameToRemove },
      { $pull: { labels: nameToRemove } }
    );

    const spamUpdate = await Spam.updateMany(
      { owner: username, labels: nameToRemove },
      { $pull: { labels: nameToRemove } }
    );

    const trashUpdate = await Trash.updateMany(
      { owner: username, labels: nameToRemove },
      { $pull: { labels: nameToRemove } }
    );

    return { mailsUpdate, draftsUpdate, spamUpdate, trashUpdate };
    //return { mailsUpdate, draftsUpdate };
  } catch (err) {
    console.error("Error deleting label in all elements:", err);
    return {};
  }
}




const updateLabel = async (id, name, userId, username) => {
  const label = await Label.findById(id).exec();
  if (!label) throw new Error("Label not found");
  if (label.userId !== userId) throw new Error("Unauthorized");



  const conflict = await Label.findOne({
    _id: { $ne: id },
    name,
    userId,
  });
  if (conflict) throw new Error("Label with this name already exists");

  const oldName = label.name;
  label.name = name;
  const savedLabel = await label.save();

  await updateLabelInAllElements(oldName, name, username);

  return savedLabel;
};

const deleteLabel = async (id, userId, username) => {
  const label = await Label.findById(id).exec();
  if (!label) throw new Error("Label not found");
  if (label.userId !== userId) throw new Error("Unauthorized");
  const oldName = label.name;

  await label.deleteOne();

  await deleteLabelInAllElements(oldName, username);

  return true;
};


const getLabelByIdWithoutUserId = async (id) => {
  return await Label.findById(id).exec(); 
};

function convertLabelsArrayToObjects(labelStrings) {
  if (!Array.isArray(labelStrings)) return [];
  return labelStrings.map((label) => ({ name: label }));
}

const searchAllLabelsArray = async (labelsArray, userId) => {
  if (!Array.isArray(labelsArray)) return false;

  const userLabels = await Label.find({ userId }).select("name").exec();
  const userLabelNames = userLabels.map((l) => l.name);

  return labelsArray.every((labelName) => userLabelNames.includes(labelName));
};

module.exports = {
  getLabels,
  getLabelById,
  addLabel,
  updateLabel,
  deleteLabel,
  getLabelByIdWithoutUserId,
  convertLabelsArrayToObjects,
  searchAllLabelsArray,
};
