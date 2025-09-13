import React, { useState, useEffect, useRef } from 'react';
import "./inbox.css"; 

const SearchResults = ({ emails }) => {
  return (
    <div className="inbox-container">
      <h2>🔎 Search Results</h2>
      <ul className="email-list">
        {emails.length === 0 && <li>No results found</li>}
        {emails.map((email, idx) => (
          <li key={email.id || idx} className="email-item">
            <span className="sender">{email.from || email.sender || "Unknown"}</span>
            <span className="subject">{email.subject || "No Subject"}</span>
            <span className="time">{email.time || ""}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchResults;
