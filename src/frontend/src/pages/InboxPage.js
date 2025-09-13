import React from "react";
//import "./InboxPage.css";

const InboxPage = () => {
    return (
        <div className="gmail-layout">
            <aside className="sidebar">
                <button className="compose-btn">+ Compose</button>
                <ul className="menu">
                    <li>📩 Inbox</li>
                    <li>⭐ Starred</li>
                    <li>📨 Sent</li>
                    <li>🗑️ Trash</li>
                </ul>
            </aside>

            <div className="content">
                <header className="topbar">
                    <input type="text" placeholder="🔍 Search mail..." className="search-bar" />
                </header>

                <section className="inbox">
                    <div className="email">
                        <span className="sender">Google</span>
                        <span className="subject">Welcome to Gmail</span>
                        <span className="time">10:30 AM</span>
                    </div>
                    <div className="email">
                        <span className="sender">Facebook</span>
                        <span className="subject">New Security Updates</span>
                        <span className="time">09:15 AM</span>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default InboxPage;
