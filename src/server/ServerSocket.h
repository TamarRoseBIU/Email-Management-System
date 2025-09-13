
// type of ICommunicate
// Add a function of Listening
#pragma once

#include "ICommunicate.h"
#include <string>

class ServerSocket : public ICommunicate {
private:
    // File descriptor of the main socket
    int server_fd;   
    // Port number - server listen
    int port;            

public:
    // Constructor that receives the port number and initializes the class
    explicit ServerSocket(int portNumber); 

     // Starts the server: creates the socket, binds it, and begins listening
    void startConnection() override;

    // Accepts a new client connection and returns its socket file descriptor
    std::pair<bool, int> getConnectionSocket() override;

    // Receives a command as string from the client
    std::pair<bool, std::string> receiveCommand(int client_fd) override;

    // Sends a response string to the client 
    bool sendResponse(int client_fd, const std::string& response) override;

    // Closes the given client socket
    void closeConnection(int client_fd) override;

    // Destructor: closes the server socket if it's still open
    ~ServerSocket() override;
};