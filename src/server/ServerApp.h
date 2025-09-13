#pragma once

#include <set>
#include <iostream>

#include "ICommand.h"
#include "BloomFilter.h"  // Include the header file for the BloomFilter class
#include "validInput.h"  // Include the header file for the input validation function   
#include "AddNewUrl.h"  // Include the header file for the AddNewUrl command
#include "CheckMatchUrl.h"  // Include the header file for the CheckMatchUrl command


class ServerApp {

public:
    // Constructor
    ServerApp();


    // Function to manage the application logic of the server
    // It will create a BloomFilter object, load data from a file, and handle commands
    // create command objects for adding, deleting and checking URLs
    // get port, bit array size and hash functions rounds
    void toDo(int port, std::size_t bitArraySize,  std::vector<std::size_t>& hashFuncRounds);


    // Destructor
    ~ServerApp()=default;
};