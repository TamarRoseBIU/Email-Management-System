// const net = require("net");
// const HOST = "127.0.0.1";
// const PORT = 9000;

const net = require("net");
const HOST = process.argv[2] || "127.0.0.1"; // First argument: IP
const PORT = process.argv[4] || 9000; // Second argument: serverSocket port

/**
 * Sends an HTTP-like command to the blacklist server.
 * @param {string} method - "POST", "DELETE", etc.
 * @param {string} url - The target URL like "www.foo.com"
 * @returns {Promise<string>} The server's response string.
 */
function sendToBlacklistServer(method, url) {
  return new Promise((resolve, reject) => {
    const client = new net.Socket();
    const message = `${method} ${url}`;

    client.connect(PORT, HOST, () => {
      client.write(message);
    });

    client.on("data", (data) => {
      const response = data.toString().trim();
      client.destroy();
      resolve(response);
    });

    client.on("error", (err) => {
      client.destroy();
      reject(err);
    });
  });
}

module.exports = { sendToBlacklistServer };
