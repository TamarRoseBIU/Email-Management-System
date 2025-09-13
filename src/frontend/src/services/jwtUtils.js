// // Utility to decode base64url
// const base64UrlDecode = (str) => {
//   // Replace URL-safe chars with standard base64 chars
//   let base64 = str.replace(/-/g, "+").replace(/_/g, "/");
//   // Pad with '=' to make length a multiple of 4
//   while (base64.length % 4) {
//     base64 += "=";
//   }
//   return atob(base64); // decode base64 string to ascii
// };

// export const decodeJWT = (token) => {
//   if (typeof token !== "string") throw new Error("Token must be a string");

//   try {
//     const payload = token.split(".")[1];
//     const decoded = JSON.parse(atob(payload));
//     return decoded;
//   } catch (error) {
//     console.error("Failed to decode JWT:", error);
//     throw error;
//   }
// };
export const decodeJWT = (token) => {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map(function (c) {
          return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Failed to decode JWT:", e);
    throw new Error("Invalid token format for decoding.");
  }
};

export const isTokenExpired = (token) => {
  try {
    const decoded = decodeJWT(token); // Use the exported decodeJWT
    if (!decoded || !decoded.exp) {
      // If token has no 'exp' claim, assume it's not meant to expire or is malformed.
      return false; // Or throw new Error("Token has no expiration claim"); depending on your strictness
    }
    const currentTime = Date.now() / 1000; // current time in seconds since epoch
    return decoded.exp < currentTime;
  } catch (e) {
    console.error("Error checking token expiration:", e);
    return true; // Treat as expired/invalid if we can't even decode it.
  }
};
