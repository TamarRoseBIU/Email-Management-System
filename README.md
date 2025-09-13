# 📧 Gmail-Like Multi-Platform Email Application

This is a comprehensive full-stack Gmail-style email application built with React (Web), Android (Mobile), Node.js backend, and JWT-based authentication. The application provides a complete email experience across multiple platforms, allowing users to register, log in, send emails, organize them with labels, and manage spam or blacklisted messages — all in clean and responsive interfaces.

## 🌐 Platform Support

- **🖥️ Web Application** - React-based responsive web interface
- **📱 Android Application** - Native Android mobile app
- **🛠️ Backend Services** - Node.js API server with blacklist filtering

## 🔧 Features Overview

### ✨ Web Frontend (React)

The web frontend is a React application that provides a Gmail-like user interface for sending, receiving, and organizing emails. It includes pages for login, registration, inbox, spam, labels, and profile viewing, all styled with responsive custom CSS.

- User authentication (register, login, logout)
- JWT token handling via AuthContext
- Inbox, Spam, Drafts, and Label Management
- Email composition and editing
- User profile viewing
- Responsive design with CSS customization
- Protected routes based on login state
- Dark/Light mode toggle
- Real-time search with suggestions
- Drag-and-drop compose window

### 📱 Android Application

The Android application provides native mobile functionality with the same core features as the web application, optimized for mobile interaction patterns.

- **Native Android UI** - Material Design components and Android-specific UI patterns
- **Touch-Optimized Interface** - Mobile-friendly gestures and interactions
- **Push Notifications** - Real-time email notifications (if implemented)
- **Offline Capabilities** - Local storage for offline viewing (if implemented)
- **Mobile Authentication** - Seamless login/registration flow
- **Cross-Platform Sync** - Synchronized data with web application
- **Mobile-Specific Features**:
  - Swipe gestures for email actions
  - Pull-to-refresh functionality
  - Mobile-optimized compose screen
  - Adaptive layouts for different screen sizes

### 🛠️ Backend (Node.js + Express)

The backend is a Node.js application built with Express.js, following the MVC architecture. It provides RESTful API endpoints for user management, email operations, label handling, and search functionality. Users can register, log in, send and update emails, and manage their own label categories.

- JWT authentication middleware
- User model with secure ID handling
- Mail, Draft, Trash models (send, get, update, delete)
- Label system per user
- Blacklist filtering for disallowed malicious URLs
- Cross-platform API compatibility

### 🛡️ Blacklist Server

A blacklist filtering mechanism that efficiently checks whether a URL is part of a blacklist. It integrates with a Bloom Filter TCP server to block blacklisted URLs from being sent. The server supports concurrent requests and ensures safe access to shared resources using locks where necessary.

## 🔐 Authentication Flow

The authentication system works consistently across all platforms:

1. On login, the server returns a JWT token
2. The frontend/mobile app stores the token and sends it with all protected API requests via the Authorization: Bearer header
3. The server decodes the token to verify the user and grant access to their data
4. Cross-platform synchronization ensures consistent user experience

# 🚀 Running the Application

📘 Project setup and execution are fully detailed in the Wiki.

## Android Application Setup

### 1. Open Android Studio

### 2. Import the Android project:

- File → Open → Select the android app directory
- Wait for Gradle sync to complete

### 3. Configure API endpoints:

- Update the API base URL in your Android app configuration
- Ensure it points to your running backend server

### 4. Build and run:

- Connect an Android device or start an emulator
- Click Run or use Ctrl+R

## 🌐 Environment Variables

The system uses environment variables to configure server addresses, ports, security keys, and other settings across all platforms. You can edit them on the .env files.

### 🔁 Backend Environment Variables (./.env)

These are used by the backend servers and infrastructure:

- `FRONTEND_PORT` – Port used by the frontend application
- `BACKEND_PORT` – Port for the backend server
- `SERVER_PORT` – Additional server port (e.g., for the blacklist service)
- `JWT_SECRET` – Secret key used for signing JWT tokens
- `BITS_ARRAY` – Bit array size for the Bloom filter
- `HASH_1`, `HASH_2` – Hash function identifiers for the Bloom filter
- `BLACKLIST_HOST` – Hostname for the blacklist service

### 🌐 Web Frontend Environment Variables (./src/frontend/.env)

These are used by the React frontend application:

- `REACT_APP_WEBSOCKET_URL` – WebSocket URL for communication with the backend
- `REACT_APP_FRONTEND_URL` – Base URL for the frontend interface
- `REACT_APP_BACKEND_URL` – URL of the backend API server
- `REACT_APP_FRONTEND_PORT` / `REACT_APP_BACKEND_PORT` – Ports for the frontend and backend apps

### 📱 Android Configuration

- The Android app configuration uses the port from the .env file.

# 📱 Platform-Specific Features

## Web Application Highlights

- **Desktop-Optimized UI**: Large screen layouts with sidebar navigation
- **Drag-and-Drop Compose**: Moveable compose window
- **Advanced Search**: Real-time search with history and suggestions
- **Keyboard Shortcuts**: Gmail-like keyboard navigation
- **Dark/Light Mode**: Theme switching capability

## Android Application Highlights

- **Mobile-First Design**: Touch-optimized interface elements
- **Gesture Navigation**: Swipe actions for email management
- **Material Design**: Native Android UI components
- **Adaptive Layouts**: Responsive design for various screen sizes
- **Mobile Notifications**: Push notification support
- **Offline Support**: Local caching for improved performance

## Shared Features Across Platforms

- **User Management**: Registration, login, profile management
- **Email Operations**: Send, receive, organize, and search emails
- **Label System**: Create and manage custom email labels
- **Spam Detection**: Automatic blacklist-based spam filtering
- **Draft Management**: Save and edit draft emails
- **Trash Management**: Soft delete with restore functionality
- **Real-time Sync**: Consistent data across all platforms

# 🔧 Core Functionality

## 📧 User Registration and Authentication

Both web and mobile platforms support:

### User Profile Requirements:

- **First/Last name**: English letters only (A–Z, a–z), no spaces
- **Username**: Alphanumeric characters only, must be unique
- **Password**: Minimum 6 characters, no spaces
- **Phone**: Exactly 10 digits starting with "05" (Israeli format)
- **Birthdate**: Valid past date in YYYY-MM-DD format
- **Gender**: "male", "female", or "other" (case-insensitive)
- **Profile Picture**: Valid image file (.jpg, .jpeg, .png, .gif)

## 📧 Email Management

### Send and Receive Emails

- Single or multiple recipients (comma-separated)
- Subject and body content
- Label assignment
- Automatic spam detection via blacklist filtering

### Email Organization

- **Inbox**: Incoming emails with read/unread status
- **Sent**: Outgoing emails
- **Drafts**: Saved draft emails
- **Starred**: Favorited emails across all folders
- **Spam**: Automatically filtered suspicious emails
- **Trash**: Deleted emails with 30-day retention

### Email Actions (Available on Both Platforms)

- ✉️ Mark as read/unread
- ⭐ Star/unstar emails
- 🗑️ Move to trash
- 🚫 Mark as spam
- 🏷️ Manage labels
- ↩️ Restore from spam/trash

## 🏷️ Label Management

- Create custom labels with color coding
- Assign multiple labels to emails
- Filter emails by label
- Label-based organization and searching

## 🔍 Search Functionality

### Advanced Search Features:

- **Global Search**: Search across all emails and drafts
- **Real-time Suggestions**: Live search results as you type
- **Search History**: Recent searches with management options
- **Case-Insensitive**: Search works regardless of text case
- **Multi-field Search**: Searches subject, body, sender, and labels

## 🚫 Spam and Blacklist Management

### Automatic Spam Detection:

1. Emails with blacklisted URLs automatically go to spam
2. Manual spam marking adds URLs to shared blacklist
3. Collaborative protection for all users
4. Restore functionality removes URLs from blacklist

# 🎨 User Interface Features

## Web Interface

- **Responsive Design**: Works on desktop, tablet, and mobile browsers
- **Dark/Light Mode**: Theme switching
- **Draggable Compose**: Moveable email composition window
- **Sidebar Navigation**: Quick access to all email folders
- **Modal Dialogs**: Profile viewing and email composition

## Android Interface

- **Material Design**: Native Android UI components
- **Touch Gestures**: Swipe actions for email management
- **Pull-to-Refresh**: Update email lists with downward swipe
- **Adaptive Navigation**: Bottom navigation or drawer based on screen size
- **Floating Action Button**: Quick compose access

# 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Validation**: Strong password requirements
- **URL Blacklisting**: Shared blacklist protection against malicious links
- **Input Validation**: Comprehensive server-side validation
- **Secure API**: Protected endpoints with authentication middleware

# 📊 Technical Architecture

## Frontend Architecture

- **Web**: React with Context API for state management
- **Android**: Native Android with modern architecture components
- **Shared**: RESTful API consumption and JWT token management

## Backend Architecture

- **API Server**: Node.js with Express.js (MVC pattern)
- **Authentication**: JWT-based with secure token handling
- **Database**: User profiles, emails, drafts, labels, and blacklist
- **Blacklist Service**: Bloom Filter TCP server for URL filtering

# 🚀 Getting Started

1. **Set up Backend**: Use Docker Compose to start all backend services
2. **Web Development**: Run `npm start` in the frontend directory
3. **Android Development**: Import project in Android Studio and build
4. **Configuration**: Update environment variables for your setup

# 🤝 Contributing

This multi-platform email application welcomes contributions for:

- Web frontend improvements
- Android app enhancements
- Backend API extensions
- Security improvements
- UI/UX enhancements
- Performance optimizations

---

_This application demonstrates modern full-stack development with cross-platform compatibility, providing a complete email solution for both web and mobile users._
