#include <gtest/gtest.h>
#include <thread>
#include <chrono>
#include <string>
#include <vector>
#include <iostream>
#include <cstring>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

#include "../src/server/ServerApp.h"

constexpr int TEST_PORT = 12345;

// Helper: connect to server and send a command
std::string sendCommand(const std::string& command) {
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == -1) {
        ADD_FAILURE() << "Failed to create socket";
        return "ERROR: Socket creation failed";
    }

    sockaddr_in serverAddr{};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(TEST_PORT);
    inet_pton(AF_INET, "127.0.0.1", &serverAddr.sin_addr);

    int status = connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr));
    if (status != 0) {
        ADD_FAILURE() << "Failed to connect to server";
        close(sock);
        return "ERROR: Connection failed";
    }

    send(sock, command.c_str(), command.size(), 0);

    char buffer[4096] = {0};
    int bytesReceived = recv(sock, buffer, sizeof(buffer) - 1, 0);
    if (bytesReceived <= 0) {
        ADD_FAILURE() << "No response received from server";
        close(sock);
        return "ERROR: No response";
    }

    close(sock);
    return std::string(buffer, bytesReceived);
}


// Launch server in background
void runServer() {
    std::vector<std::size_t> hashParams = {1, 2};  // Example params
    ServerApp app;
    app.toDo(TEST_PORT, 1000, hashParams);
}

class ServerAppTest : public ::testing::Test {
protected:
    static void SetUpTestSuite() {
        serverThread = std::thread(runServer);
        std::this_thread::sleep_for(std::chrono::seconds(1)); // Allow server to boot up
    }

    static void TearDownTestSuite() {
        // Server doesn't exit automatically; may require signal or be manually stopped
        // In real testing, use a kill-switch or mock ICommunicate
        pthread_cancel(serverThread.native_handle());
        serverThread.join();
    }

    static std::thread serverThread;
};

std::thread ServerAppTest::serverThread;

// ======== ACTUAL TESTS ========

TEST_F(ServerAppTest, PostUrlCommand) {
    std::string command = "POST http://example.com";
    std::string response = sendCommand(command);
    

    std::string expectedResponse = "201 Created";
    
    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, GetUrlCommand) {
    std::string command = "GET http://example.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "200 Ok\n\n\ntrue true";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, DeleteUrlCommand) {
    std::string command = "DELETE http://example.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "204 No Content";

    EXPECT_EQ(response, expectedResponse);
}


TEST_F(ServerAppTest, invalidCommand) {
    std::string command = "delete http://example.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}


TEST_F(ServerAppTest, invalidUrlPost) {
    std::string command = "POST haha";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, invalidUrlCheckMatch) {
    std::string command = "GET haha";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}


// TEST_F(ServerAppTest, emptyCommand) {
//     std::string command = "";
//     std::string response = sendCommand(command);

//     std::string expectedResponse = "400 Bad Request";

//     EXPECT_EQ(response, expectedResponse);
// TEST_F(ServerAppTest, EmptyCommand) {
//     std::string command = "";
//     std::string response = sendCommand(command);

//     std::string expectedResponse = "400 Bad Request";

//     EXPECT_EQ(response, expectedResponse);
// }

TEST_F(ServerAppTest, PostInvalidUrlFormat) {
    std::string command = "POST http:/example.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, GetNonExistentUrl) {
    std::string command = "GET notvalid";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, DeleteNonExistentUrl) {
    std::string command = "DELETE http://nonexistent.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "404 Not Found";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, PostEmptyUrl) {
    std::string command = "POST ";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, GetEmptyUrl) {
    std::string command = "GET ";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, DeleteEmptyUrl) {
    std::string command = "DELETE ";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, PostUrlWithSpaces) {
    std::string command = "POST http://example .com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, GetUrlWithSpaces) {
    std::string command = "GET http://example .com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, DeleteUrlWithSpaces) {
    std::string command = "DELETE http://example .com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, UnsupportedCommand) {
    std::string command = "PUT http://example.com";
    std::string response = sendCommand(command);

    std::string expectedResponse = "400 Bad Request";

    EXPECT_EQ(response, expectedResponse);
}

TEST_F(ServerAppTest, CommandWithExtraSpaces) {
    std::string command = "  POST   http://example.com  ";
    std::string response = sendCommand(command);

    std::string expectedResponse = "201 Created";

    EXPECT_EQ(response, expectedResponse);
}

