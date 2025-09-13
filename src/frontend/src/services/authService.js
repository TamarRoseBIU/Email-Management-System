const port = process.env.REACT_APP_BACKEND_PORT; // Use the port from .env or default to 8080
const BASE_URL = `http://localhost:${port}/api`; // Base URL for the API
import { decodeJWT, isTokenExpired } from "./jwtUtils"; // wherever you put the decodeJWT function

export const login = async (username, password) => {
  try {
    const response = await fetch(`${BASE_URL}/tokens`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      const errorText = await response.text();

      // Try to parse as JSON first, o.w. plain text
      try {
        const errorJson = JSON.parse(errorText);
        return { error: errorJson.message || errorJson.error || errorText };
      } catch {
        return { error: errorText };
      }
    }

    return await response.json();
  } catch (error) {
    return { error: error.message };
  }
};

export const register = async (formData) => {
  try {
    const response = await fetch(`${BASE_URL}/users`, {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      const errorText = await response.text();

      // Try to parse as JSON first, fallback to plain text
      try {
        const errorJson = JSON.parse(errorText);
        return { error: errorJson.message || errorJson.error || errorText };
      } catch {
        return { error: errorText };
      }
    }

    return await response.json();
  } catch (err) {
    return { error: err.message };
  }
};
// export const getUser = async (token) => {
//   try {
//     const response = await fetch(`${BASE_URL}/users/me`, {
//       method: "GET",
//       headers: {
//         Authorization: `Bearer ${token}`,
//       },
//     });

//     if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

//     return await response.json();
//   } catch (error) {
//     console.error("Get user error:", error);
//     return { error: error.message };
//   }
// };
// export const authFetch = async (url, options = {}, token) => {
//   return fetch(`${BASE_URL}${url}`, {
//     ...options,
//     headers: {
//       ...(options.headers || {}),
//       Authorization: `Bearer ${token}`,
//     },
//   });
// };

// old func, with expired tokens
// export const getUser = async (token) => {
//   try {
//     const decoded = decodeJWT(token);
//     const userId = decoded.id;

//     const response = await fetch(`${BASE_URL}/users/${userId}`, {
//       headers: {
//         Authorization: `Bearer ${token}`,
//       },
//     });

//     if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
//     return await response.json();
//   } catch (error) {
//     console.error("Get user error:", error);
//     return { error: error.message };
//   }
// };

export const getUser = async (token) => {
  try {
    // 1. First, check if the token is even present or already expired
    if (!token || isTokenExpired(token)) {
      return null;
    }

    // 2. Decode the token to get userId (if not already handled by isTokenExpired)
    //    We call decodeJWT again here to ensure we have the `id` from a valid token.
    //    If `isTokenExpired` threw an error for a malformed token, this won't be reached.
    const decoded = decodeJWT(token);
    const userId = decoded.id; // Make sure your JWT payload has an 'id' field

    // 3. Make the API call
    const response = await fetch(`${BASE_URL}/user`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // 4. Handle non-OK HTTP responses from the backend
    if (!response.ok) {
      let errorMessage = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        if (errorData && errorData.message) {
          errorMessage = errorData.message; // Use backend's error message if available
        }
      } catch (jsonError) {
        // Ignore if response body is not JSON
      }
      throw new Error(errorMessage);
    }

    // 5. Return user data if successful
    return await response.json();
  } catch (error) {
    console.error("Get user error:", error);
    // Ensure an object with an 'error' property is always returned on failure
    return {
      error:
        error.message ||
        "Failed to retrieve user data due to an unknown error.",
    };
  }
};
