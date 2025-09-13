// sequence of letters in English without spaces
function isValidName(name) {
  return typeof name === "string" && /^[A-Za-z]+$/.test(name);
}

// check phone number
function isValidPhoneNumber(phoneNumber) {
  return typeof phoneNumber === "string" && /^05\d{8}$/.test(phoneNumber);
}

// check that the gender includes here: ["male", "female", "other"]
function isValidGender(gender) {
  return typeof gender === "string" && ["male", "female", "other"].includes(gender.toLowerCase());
}

// check if it is valid birth date
function isValidBirthDate(birthDate) {
  if (typeof birthDate !== "string" && !/^\d{4}-\d{2}-\d{2}$/.test(birthDate))
  {
    return false;
  }
  const date = new Date(birthDate);
  const now = new Date();
  // the date is in the future
  if (date > now) {
    return false;
  }
  return true;
}

// check that the password is valid
function isValidPassword(password) {
  return typeof password === "string" &&
    password.length >= 6 && !/\s/.test(password); 
}

// check the username - only letters in English and numbers
function isValidUsername(username) {
  return typeof username === "string" && /^[a-zA-Z0-9]+$/.test(username);
}

// check if the profile picture is a valid image file
function isValidProfilePic(profilePic) {
  if (typeof profilePic !== "string") return false;

  // Check file extension
  return /\.(jpg|jpeg|png|gif)$/i.test(profilePic);
}


module.exports = {
  isValidName,
  isValidUsername,
  isValidPassword,
  isValidPhoneNumber,
  isValidBirthDate,
  isValidGender,
  isValidProfilePic
};




