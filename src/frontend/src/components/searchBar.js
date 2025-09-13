import React, { useState, useEffect, useRef  , useContext} from "react";
import "./searchBar.css";
import { AuthContext } from "../context/AuthContext";

const SearchBar = ({ onSearch, onResults, onDisplayedQuery }) => {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const wrapperRef = useRef(null);
  const [mailSuggestions, setMailSuggestions] = useState([]);
  const [displayedQuery, setDisplayedQuery] = useState("");
  const [shouldSaveSearch, setShouldSaveSearch] = useState(false);
  const { user } = useContext(AuthContext);

  const userId =
    localStorage.getItem("user-id") || "c0e33a7d-22be-4149-a757-5905a5b07a65";
  const port = process.env.REACT_APP_BACKEND_PORT;
  const API_URL = `http://localhost:${port}/api/history`;
  const MAIL_SEARCH_URL = `http://localhost:${port}/api/mails/search/`;
  // const MAIL_SEARCH_URL = "http://localhost:8080/api/searchAll/query/";


  // port

  // Function to fetch recent searches from the server
  const fetchRecentSearches = async () => {
    try {
      const response = await fetch(API_URL, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      if (!response.ok) throw new Error("Failed to fetch search history");
      const data = await response.json();
      // setSuggestions(data);
      setSuggestions(data.searches || []);
    } catch (err) {
      console.error("Failed to load recent searches", err);
    }
  };
  // Function to clear search history
  const fetchClearHistory = async () => {
    try {
      await fetch(API_URL, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      setSuggestions([]);
      setShowSuggestions(false);
    } catch (err) {
      console.error("Failed to clear search history", err);
    }
  };

  const fetchHandleRemoveQuery = async (queryToRemove) => {
    try {
      await fetch(`${API_URL}/${encodeURIComponent(queryToRemove)}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${user.token}`,
        },
      });
      setSuggestions(suggestions.filter((q) => q !== queryToRemove));
    } catch (err) {
      console.error("Failed to remove query from history", err);
    }
  };

  const fetchSearchSuggestions = async (value, saveSearch = false) => {
    if (!value.trim()) {
      setMailSuggestions([]);
      return [];
    }
    try {
      const response = await fetch(
        `${MAIL_SEARCH_URL}${encodeURIComponent(value)}`,
        {
          headers: {
            Authorization: `Bearer ${user.token}`,
            "save-search": saveSearch.toString(),
          },
        }
      );

      if (!response.ok) throw new Error("Failed to fetch mails");
      const result = await response.json();
      const mails = result.data || [];
      setMailSuggestions(mails);
      // onResults(mails);
      return mails;
    } catch (err) {
      console.error("Error fetching mail suggestions", err);
      onResults([]);
      return [];
    }
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleFocus = () => {
    fetchRecentSearches();
    setShowSuggestions(true);
  };

  const handleKeyDown = async (e) => {
    if (e.key === "Enter") {
      //  e.preventDefault();
      setShowSuggestions(false);

      if (query.trim()) {
        const results = await fetchSearchSuggestions(query, true);
        onResults(results);
        onSearch(query);
        onDisplayedQuery(query);
        setDisplayedQuery(query);
        // slice?
        fetchRecentSearches();
      }
    }
  };

  const handleSuggestionClick = async (suggestion) => {
    onDisplayedQuery(suggestion);
    setDisplayedQuery(suggestion);
    const results = await fetchSearchSuggestions(suggestion, true);
    setQuery(suggestion);
    onSearch(suggestion);
    onResults(results);
    setShowSuggestions(false);
    fetchRecentSearches();
  };

  const handleMailSuggestionClick2 = (selectedMail) => {
  setQuery(selectedMail.subject || "");
  setMailSuggestions([]); 
  onResults([selectedMail]); 
  onDisplayedQuery(selectedMail.subject || "");
  setDisplayedQuery(selectedMail.subject || "");
  setShowSuggestions(false);
};
const handleClickOnMailSuggestion = (mail) => {
  if (!mail || typeof mail !== "object" || !mail.id) {
    console.error("Invalid mail clicked:", mail);
    return;
  }

  const subjectToDisplay = mail.subject || "No Subject";

  setQuery(subjectToDisplay);               
  setDisplayedQuery(subjectToDisplay);      
  onDisplayedQuery(subjectToDisplay);       
  onSearch(subjectToDisplay);               
  onResults([mail]);                        
  setShowSuggestions(false);                
  setMailSuggestions([]);                   
};


  const handleChange = (e) => {
    setQuery(e.target.value);
    fetchSearchSuggestions(e.target.value, false);
    fetchRecentSearches();
    setShowSuggestions(true);
  };

  return (
    <div className="search-wrapper" ref={wrapperRef}>
      <input
        type="text"
        placeholder="🔍 Search mail..."
        className="search-bar"
        value={query}
        onChange={handleChange}
        onFocus={handleFocus}
        onKeyDown={handleKeyDown}
      />
      {showSuggestions && (
        <ul className="search-suggestions">
          {query.trim() === "" && suggestions.length > 0 && (
            <>
              {suggestions.map((sugg, idx) => (
                <li
                  key={idx}
                  onClick={() => handleSuggestionClick(sugg)}
                  className="suggestion-item"
                >
                  <span
                    className="suggestion-text"
                    onClick={() => handleSuggestionClick(sugg)}
                  >
                    {sugg}
                  </span>
                  <button
                    className="remove-btn"
                    onClick={(e) => {
                      e.stopPropagation();
                      fetchHandleRemoveQuery(sugg);
                    }}
                    title="Remove from history"
                  >
                    ❌
                  </button>
                </li>
              ))}
              <li
                className="suggestion-item clear-history"
                onClick={fetchClearHistory}
              >
                🗑️ Clear History
              </li>
            </>
          )}

          {query.trim() !== "" && mailSuggestions.length > 0 && (
            <>
              {mailSuggestions.map((mail, idx) => (
                <li
                  key={`mail-${idx}`}
                  className="suggestion-item mail-result"
                  onClick={() => handleClickOnMailSuggestion(mail)}
                >
                  <span className="icon">✉️</span>
                  <div className="mail-preview">
                    <span className="mail-subject">
                      {mail.subject || "No Subject"}
                    </span>
                    <span className="mail-from">{mail.from || "Unknown"}</span>
                  </div>
                </li>
              ))}
            </>
          )}
        </ul>
      )}
    </div>
  );
};

export default SearchBar;
