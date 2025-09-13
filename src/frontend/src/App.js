import React, { useContext } from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";
// homePage.js or App.js
import HomePage from "./pages/HomePage";
import WelcomePage from "./pages/WelcomePage";
// import Login from "./components/Login_old";
import Login from "./components/Login";
import Register from "./components/Register";
// import Inbox from "./pages/InboxPage";
//import Spam from "./pages/Spam";
//import LabelManager from "./components/Labels/LabelManager";
//import Navbar from "./components/Shared/Navbar";
import UserMenu from "./components/UserMenu";
import ProfilePage from "./pages/ProfilePage";
import SearchResults from "./components/searchAll";
import PrivateRoute from "./components/PrivateRoute";
// import AuthPage from "./pages/AuthPage";
import { AuthProvider, AuthContext } from "./context/AuthContext";
// ⛔️ We cannot use useLocation() here directly
// So we wrap AppContent inside <Router> instead
const Spinner = () => (
  <div style={{ textAlign: "center", padding: "2rem" }}>Loading…</div>
);

function AppContent() {
  //const { token } = useContext(AuthContext);
  const { token, loading } = useContext(AuthContext);
  if (loading) return <Spinner />;
  const isLoggedIn = !!token;

  return (
    <>
      {/* {isLoggedIn && <Navbar />} */}
      {/* {isLoggedIn && <UserMenu />} */}
      {isLoggedIn}
      <Routes>
        <Route
          path="/"
          element={isLoggedIn ? <Navigate to="/home" /> : <Login />}
        />
        <Route
          path="/login"
          element={isLoggedIn ? <Navigate to="/home" /> : <Login />}
        />
        <Route
          path="/register"
          element={isLoggedIn ? <Navigate to="/home" /> : <Register />}
        />

        <Route
          path="/home"
          element={
            <PrivateRoute>
              <HomePage view="inbox" />
            </PrivateRoute>
          }
        />
        <Route
          path="/drafts"
          element={
            <PrivateRoute>
              <HomePage view="drafts" />
            </PrivateRoute>
          }
        />
        <Route
          path="/sent"
          element={
            <PrivateRoute>
              <HomePage view="sent" />
            </PrivateRoute>
          }
        />
        <Route
          path="/starred"
          element={
            <PrivateRoute>
              <HomePage view="starred" />
            </PrivateRoute>
          }
        />
        <Route
          path="/spam"
          element={
            <PrivateRoute>
              <HomePage view="spam" />
            </PrivateRoute>
          }
        />
        <Route
          path="/trash"
          element={
            <PrivateRoute>
              <HomePage view="trash" />
            </PrivateRoute>
          }
        />
        <Route
          path="/search/query/:query"
          element={
            <PrivateRoute>
              <HomePage view="search" />
            </PrivateRoute>
          }
        />
        <Route
          path="/search/label/:labelName"
          element={
            <PrivateRoute>
              <HomePage view="search-label" />
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <ProfilePage />
            </PrivateRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </>
  );
}
// localStorage.removeItem("token");
if (!sessionStorage.getItem("frontendStarted")) {
  localStorage.removeItem("token");
  sessionStorage.setItem("frontendStarted", "true");
  console.log("🧼 Token cleared on first load after npm start");
}
localStorage.setItem("api-port", process.env.REACT_APP_BACKEND_PORT);

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppContent />
      </Router>
    </AuthProvider>
  );
}

export default App;
