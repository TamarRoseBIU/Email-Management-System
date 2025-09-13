(async () => {
  try {
    require("dotenv").config({ path: "../../.env" });
    console.log("🔄 Starting server setup...");

    const express = require("express");
    const bodyParser = require("body-parser");
    const cors = require("cors");
    const path = require("path");
    const connectDB = require("./config/db");
    const { loadBlacklistFromFile } = require("./services/blacklistService");
    const { startTrashCleanup } = require("./utils/cronJobs");

    // connect to db
    console.log("📡 Connecting to MongoDB...");
    await connectDB();
    console.log("✅ Connected to MongoDB");

    // only after connecting to db
    console.log("📦 Loading routes...");
    const usersRoutes = require("./routes/usersRoutes");
    const mailsRoutes = require("./routes/mailsRoutes");
    const blacklistRoutes = require("./routes/blacklistRoute");
    const labelsRoutes = require("./routes/labelsRoute");
    const searchRoutes = require("./routes/searchRoute");
    const historyRoutes = require("./routes/historyRoute");
    const draftsRoutes = require("./routes/draftsRoute");
    const starredRoutes = require("./routes/starredRoute");
    const spamRoutes = require("./routes/spamsRoute");
    const trashRoutes = require("./routes/trashRoute");
    const searchAllRoutes = require("./routes/searchAllRoute");
    const objectsRoutes = require("./routes/objectsRoute");

    const app = express();
    const corsPort = process.env.REACT_APP_FRONTEND_PORT || 3000;
    const corsOrigin = `http://localhost:${corsPort}`;

    console.log("🧱 Setting up middleware...");
    app.use(express.json());
    app.use(express.urlencoded({ extended: true }));
    app.set("json spaces", 2);
    app.use((req, res, next) => {
      res.removeHeader("Date");
      next();
    });
    app.use(bodyParser.json());
    app.use(cors({
      origin: corsOrigin,
      methods: ["GET", "POST", "PUT", "DELETE", "PATCH"],
      credentials: true,
    }));

    app.use("/uploads", express.static(path.join(__dirname, "./uploads")));

    console.log("🧩 Setting up routes...");
    app.use("/api/history", historyRoutes);
    app.use("/api/mails", mailsRoutes);
    app.use("/api/drafts", draftsRoutes);
    app.use("/api/spam", spamRoutes);
    app.use("/api/trash", trashRoutes);
    app.use("/api/mails/search", searchRoutes);
    app.use("/api/blacklist", blacklistRoutes);
    app.use("/api/labels", labelsRoutes);
    app.use("/api/starred", starredRoutes);
    app.use("/api/searchAll", searchAllRoutes);
    app.use("/api/objects", objectsRoutes);
    app.use("/api", usersRoutes);

    console.log("📂 Loading blacklist from file...");
    await loadBlacklistFromFile();
    console.log("✅ Blacklist loaded");

    console.log("🗑️ Starting trash cleanup job...");
    startTrashCleanup();

    const port = process.argv[3] || process.env.BACKEND_PORT;
    if (isNaN(port) || port < 1 || port > 65535) {
      console.error("❌ Invalid port number.");
      process.exit(1);
    }

    app.listen(port, () => {
      console.log(`🚀 Server listening on port ${port}`);
    });
  } catch (err) {
    console.error("❌ Fatal error during server startup:");
    console.error(err);
    process.exit(1);
  }
})();

