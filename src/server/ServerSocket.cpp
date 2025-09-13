
#include "ServerSocket.h"
#include <iostream>
#include <unistd.h>       // close()
#include <sys/socket.h>   // socket, bind, listen, accept, recv, send
#include <netinet/in.h>   // sockaddr_in
#include <arpa/inet.h>    // htons, inet_ntoa
#include <cstring>        // memset

// Constructor just saves the port and sets the socket descriptor to -1
ServerSocket::ServerSocket(int portNumber)
    : port(portNumber), server_fd(-1) {}

// Create the server socket, bind it to the port and start listening
void ServerSocket::startConnection() {
    // Create the TCP socket
    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        perror("Error creating socket");
        exit(1);
    }

    // Prepare the sockaddr_in struct for binding
    struct sockaddr_in server_addr;
    // Clear it
    memset(&server_addr, 0, sizeof(server_addr)); 

    // IPv4 - the regular ip
    server_addr.sin_family = AF_INET;
    // Accept messages coming to any of the computer's IP addresses         
    server_addr.sin_addr.s_addr = INADDR_ANY; 
    // Convert the port number to "network byte order" - big-endian,  using htons
    // computers might store numbers in different ways (little vs big endian)
    server_addr.sin_port = htons(port);       

    // Bind the socket to the port
    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Error binding socket");
        close(server_fd);
        exit(1);
    }

    // Start listening for connections (5 = max queue length) 
    int max_queue_length = 5; // in next assignment we will change it
    if (listen(server_fd, max_queue_length) < 0) {
        perror("Error listening on socket");
        close(server_fd);
        exit(1);
    }
}

// Accept a client and return its socket descriptor
std::pair<bool, int> ServerSocket::getConnectionSocket() {
    struct sockaddr_in client_addr;
    socklen_t addr_len = sizeof(client_addr);

    int client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &addr_len);
    if (client_fd < 0) {
        // perror("Error accepting client");
        return {false, -1};
    }
    return {true, client_fd};
}

// Receive a message from the given socket
std::pair<bool, std::string> ServerSocket::receiveCommand(int client_fd) {
    char buffer[4096];
    // Clear
    memset(buffer, 0, sizeof(buffer));

    int bytes_received = recv(client_fd, buffer, sizeof(buffer), 0);
    if (bytes_received < 0) {
        // perror("Error receiving data");
        return {false, ""};
    } else if (bytes_received == 0) {
        // perror("Client disconnected.");
        return {false, ""};
    }

    //return std::string(buffer, bytes_received);
    return {true, std::string(buffer, bytes_received)};
}

// Send a message back to the given socket
bool ServerSocket::sendResponse(int client_fd, const std::string& response) {
    int bytes_sent = send(client_fd, response.c_str(), response.size(), 0);
    if (bytes_sent < 0) {
        // perror("Error sending response");
        return false;
    }
    return true;
}

// Close the given socket
void ServerSocket::closeConnection(int client_fd) {
    if (client_fd >= 0) {
        close(client_fd);
    }
}

// Destructor - close the server socket if needed
ServerSocket::~ServerSocket() {
    if (server_fd >= 0) {
       close(server_fd);
    }
}
