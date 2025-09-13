import React from "react";
import Inbox from "../components/inbox"; // Ensure correct path
import SearchBar from "../components/searchBar"; 
import "./HomePage.css";

const HomePage = () => {
    return (
        <div className="main-layout">
            <aside className="sidebar">
                <button className="compose-btn">Compose</button>
                <ul className="menu">
                    <li>📩 Inbox</li>
                    <li>⭐ Starred</li>
                    <li>📨 Sent</li>
                    <li>📝 Draft</li>
                    <li>🗑️ Trash</li>
                </ul>
            </aside>

            <div className="content">
                <header className="topbar">
                    <img src="../../public/gmail_logo2.png" alt="Logo" className="logo" /> 
                    <SearchBar onSearch={(query) => console.log("Searching for:", query)} />

                </header>

                <section className="inbox-section">
                    <Inbox />
                </section>
            </div>
        </div>
    );
};

export default HomePage;
