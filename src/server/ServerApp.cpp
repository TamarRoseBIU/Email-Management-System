
#include "ServerApp.h"
#include <sstream>
#include <vector>
#include <string>
#include <iostream>
#include "IIOHandler.h"
#include "FileIOHandler.h"
#include "AddHashParam.h"
#include "ICommunicate.h"
#include "ServerSocket.h"
#include "CommandsMenu.h"
#include "CommandTranslator.h"
#include "Manage_Command.h"
#include <mutex>
#include "ThreadPool.h"
#define MAX_CLIENTS 100

ServerApp::ServerApp() {}

// Function to manage the application logic of the server
// It will create a BloomFilter object, load data from a file, and handle commands
// create command objects for adding, deleting and checking URLs
// get port, bit array size and hash functions rounds
void ServerApp::toDo(int port, std::size_t bitArraySize, std::vector<std::size_t> &hashFuncRounds)
{
    // First, load data from the file

    IIOHandler *fileIoHandler = new FileIOHandler("data/data.txt");
    // create bloomfilter object
    BloomFilter bloomFilterManager = fileIoHandler->loadData(bitArraySize); // create file handler object

    // used to interpret user input, and return the desired output
    Manage_Command command_manager(bloomFilterManager);

    // create hash functions according to the user input
    // copy the vector from the second element - from there the parameters are used for the hash functions
    std::vector<size_t> hashFunctionParams(hashFuncRounds.begin(), hashFuncRounds.end());
    add_all_hash_functions(bloomFilterManager, hashFunctionParams); // add the hash functions to the bloom filter

    // Turning on the bits of the reserved URLs
    // ICommand* postCommand = command_manager.getPostCommandForInit();
    for (const auto &url : bloomFilterManager.getInsertedUrls())
    {
        bloomFilterManager.updateBitArray(url);
        // postCommand->execute(url.getUrl());  // add the urls to the bloom filter
    }

    // This is where the socket will start
    // Create an instance of ServerSocket class
    ICommunicate *serverSocket = new ServerSocket(port); // port given at first argument
    serverSocket->startConnection();
    std::mutex mtx;

    // create a thread pool in order to handle multiple clients at the same time
    ThreadPool threadPoolClients(MAX_CLIENTS);

    while (true)
    {
        // Accept the client connection once before entering the loop
        std::pair<bool, int> connectionResult = serverSocket->getConnectionSocket();
        bool connectionSuccess = connectionResult.first;
        int clientFd = connectionResult.second;
        // connection failed - continue to try connection
        if (!connectionSuccess)
        {
            continue;
        }
        threadPoolClients.enqueue([clientFd, serverSocket, &command_manager, fileIoHandler, &bloomFilterManager, &mtx]()
        {
            bool clientConnected = true;
            while(clientConnected){
                try{
                    // Receives the requested command from the client and status
                    std::pair<bool, std::string>result = serverSocket->receiveCommand(clientFd);
                    if (!result.first) {
                        // status is false - client disconnected
                        clientConnected = false;
                        break;
                    }
                    std::string input = result.second;
                    std::string response;
                    {
                        // maybe it can be Immutable 
                        std::lock_guard<std::mutex> lock(mtx);
                        // here the class manage translate the command - execute it and return a response in the appropriate format
                        response = command_manager.process_request(input);
                    }
                    // send the response to the client
                    bool sendSuccess = serverSocket->sendResponse(clientFd, response);
                    if (!sendSuccess) {
                        // status is false - client disconnected
                        clientConnected = false;
                        break;
                    }
                    {
                        // update bloom filter after the command
                        // Save the data to the file after each command
                        std::lock_guard<std::mutex> lock(mtx);
                        fileIoHandler->saveData(bloomFilterManager);
                    }
                }
                catch(const std::exception& e){
                    clientConnected = false;
                }
            }
            serverSocket->closeConnection(clientFd); });
    }
    // Close the connection after finishing all communication
    delete serverSocket;
    // Destructor will closing server_fd
    // delete fileIoHandler
    delete fileIoHandler;
}