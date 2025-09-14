# 📧 Email-Management System

A full-stack, Gmail-inspired email system with web and Android clients, backed by Node.js and MongoDB. Designed for cross-platform use with JWT-based authentication, real-time synchronization, and advanced email management features.

For full documentation, screenshots, and setup instructions, see the [Wiki](https://github.com/TamarRoseBIU/Email-Management-System/wiki).

---

## 🚀 Quick Start
- [Getting Started](Getting-Started) – Setup and installation guide

---

## Platforms
- 📱 Android (Kotlin, Jetpack Compose, Room)  
- 💻 React Web Client (responsive design)  
- ⚙️ Node.js backend with MongoDB  
- 🐳 Dockerized backend services for consistent deployment

---

## Core Features
- **User Management**: Registration, login, JWT-based authentication, profile management  
- **Email Operations**: Send, receive, organize emails; drafts, trash, starred items  
- **Label System**: Custom labels with multi-label support and filtering  
- **Spam & Security**: Automatic blacklist filtering via Bloom Filter, manual spam reporting  
- **Search**: Real-time suggestions, multi-field search, case-insensitive  
- **Cross-Platform Sync**: Data consistency between web and mobile  

---

## Tech Stack
| Component       | Technologies                            |
|-----------------|-----------------------------------------|
| Web Frontend    | React                                   |
| Mobile Frontend | Android                                 |
| Backend         | Node.js, Express, MongoDB               |
| DevOps          | Docker, Docker Compose                  |
| Authentication  | JWT                                     |

---

## Architecture Overview
- **Client-Server Architecture**: Web and mobile clients communicate with a RESTful Node.js backend  
- **Authentication**: JWT-secured endpoints for cross-platform sessions  
- **Backend Services**: Node.js + Express, with MVC structure and modular routes  
- **Database**: MongoDB for users, emails, labels, drafts, and blacklist  
- **Blacklist Service**: TCP Bloom Filter server for URL spam detection  
- **DevOps**: Dockerized backend and database for reproducible environments  

---

## Running the Application
- **Backend**: Start services with Docker Compose  
- **Web Client**: `npm start` in the frontend directory  
- **Android**: Import project in Android Studio and build/run on device or emulator  
- **Configuration**: Set environment variables in `.env` files for backend, frontend, and Android  

---

## Highlights
- Full-stack cross-platform development with production-ready architecture  
- Advanced email management including labels, spam detection, and real-time search  
- Secure authentication and token management (JWT)  
- Docker-based environment for scalable deployment  
- Modern web (React) and mobile (Android) UI/UX practices

---

_For full feature details, screenshots, and platform-specific guides, here's the [Wiki](https://github.com/TamarRoseBIU/Email-Management-System/wiki)._


