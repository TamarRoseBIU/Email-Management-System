
#ifndef ICOMMUNICATE_H
#define ICOMMUNICATE_H

#include <string>

class ICommunicate {
public:

    // Create a socket, func return its number
    virtual std::pair<bool, int> getConnectionSocket() = 0;

    // Called to start Connection 
    virtual void startConnection() = 0;

    // Receive a message (contain command) from a socket
    virtual std::pair<bool, std::string> receiveCommand(int client_fd) = 0;

    // Send a response to a socket, after getting a message from it
    virtual bool sendResponse( int client_fd, const std::string& response) = 0;

    // Close a socket 
    virtual void closeConnection(int client_fd) = 0;

    // Create a Destructor
    virtual ~ICommunicate() = default;
};

#endif // ICOMMUNICATE_H
