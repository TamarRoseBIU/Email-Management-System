// export default HomePage;
import { useNavigate, useParams } from "react-router-dom";
import React, { useState, useEffect } from "react";
import Inbox from "../components/inbox";
import Drafts from "../components/Drafts";
import SentMails from "../components/SentMails";
import SearchBar from "../components/searchBar";
import SearchResults from "../components/searchResult";
import SearchAll from "../components/searchAll";
import Spam from "../components/spam";
import Trash from "../components/Trash";
import LabelContainer from "../components/LabelContainer";
import UserMenu from "../components/UserMenu";
import "./HomePage.css";
import ComposeModal from "../components/ComposeModal";
import Starred from "../components/StarredMails";

const HomePage = ({ view }) => {
  const [searchResults, setSearchResults] = React.useState([]);
  const [searchQuery, setSearchQuery] = React.useState("");
  const [displayedQuery, setDisplayedQuery] = React.useState("");
  const [isComposeOpen, setIsComposeOpen] = useState(false);
  const navigate = useNavigate();
  const { labelName } = useParams();

  // Clear search when navigating between views
  useEffect(() => {
    setSearchQuery("");
    setSearchResults([]);
    setDisplayedQuery("");
  }, [view, labelName]);

  const handleSearch = (query) => setSearchQuery(query);
  const handleResults = (results) => setSearchResults(results);
  const handleDisplayedQuery = (query) => setDisplayedQuery(query);

  const handleMenuClick = (path) => {
    setSearchQuery("");
    setSearchResults([]);
    setDisplayedQuery("");
    navigate(path);
  };

  // Callback for when labels are clicked - clears search state
  const handleLabelClick = () => {
    setSearchQuery("");
    setSearchResults([]);
    setDisplayedQuery("");
  };

  let ComponentToRender;

  switch (view) {
    case "inbox":
      ComponentToRender = Inbox;
      break;
    case "drafts":
      ComponentToRender = Drafts;
      break;
    case "spam":
      ComponentToRender = Spam;
      break;
    case "starred":
      ComponentToRender = Starred;
      break;
    case "sent":
      ComponentToRender = SentMails;
      break;
    case "trash":
      ComponentToRender = Trash;
      break;
    case "search-label":
      ComponentToRender = () => <SearchAll type="label" term={labelName} />;
      break;
    default:
      ComponentToRender = () => <div>404 - Not Found</div>;
  }

  return (
    <div className="main-layout">
      <aside className="sidebar">
        <button className="compose-btn" onClick={() => setIsComposeOpen(true)}>
          Compose
        </button>
        <ul className="menu">
          <li>
            <button
              onClick={() => handleMenuClick("/home")}
              className={!labelName && view === "inbox" ? "active" : ""}
            >
              📩 Inbox
            </button>
          </li>
          <li>
            <button
              onClick={() => handleMenuClick("/starred")}
              className={view === "starred" ? "active" : ""}
            >
              ⭐ Starred
            </button>
          </li>
          <li>
            <button
              onClick={() => handleMenuClick("/sent")}
              className={view === "sent" ? "active" : ""}
            >
              📨 Sent
            </button>
          </li>
          <li>
            <button
              onClick={() => handleMenuClick("/drafts")}
              className={view === "drafts" ? "active" : ""}
            >
              📝 Draft
            </button>
          </li>
          <li>
            <button
              onClick={() => handleMenuClick("/trash")}
              className={view === "trash" ? "active" : ""}
            >
              🗑️ Trash
            </button>
          </li>
          <li>
            <button
              onClick={() => handleMenuClick("/spam")}
              className={view === "spam" ? "active" : ""}
            >
              🚫 Spam
            </button>
          </li>
        </ul>
        <LabelContainer onLabelClick={handleLabelClick} />
      </aside>

      <div className="content">
        <header className="topbar">
          <img src="/gmail_logo.png" alt="Logo" className="logo" />
          <SearchBar
            onSearch={handleSearch}
            onResults={handleResults}
            onDisplayedQuery={handleDisplayedQuery}
          />
          <div className="topbar-right">
            <UserMenu />
          </div>
        </header>

        <section className="search-section">
          {searchQuery.trim() !== "" ? (
            <SearchResults
              emails={searchResults}
              query={searchQuery}
              displayQuery={displayedQuery}
            />
          ) : (
            <ComponentToRender />
          )}
        </section>
      </div>
      {isComposeOpen && (
        <ComposeModal onClose={() => setIsComposeOpen(false)} />
      )}
    </div>
  );
};

export default HomePage;
