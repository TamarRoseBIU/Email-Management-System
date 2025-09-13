#include "App.h"
#include <sstream>
#include <vector>
#include <string>
#include <iostream>
#include "IIOHandler.h"
#include "FileIOHandler.h"
#include "AddHashParam.h"
#include "CommandTranslator.h"

Application::Application() {}

void Application::toDo() {
    std::vector<int> numericParams = getInputWithAtLeastTwoNumbers();

    std::string line;

    // first parameter is the size of the bit array
    std::size_t bitsArraySize = numericParams[0]; // size of the bit array

    // BloomFilter bloomFilterManager(bitsArraySize); // create bloomfilter object
    
    //IIOHandler* fileIoHandler = new FileIOHandler("/app/build/data/data.txt");
    IIOHandler* fileIoHandler = new FileIOHandler("../data.txt");
    // create bloomfilter object
    BloomFilter bloomFilterManager = fileIoHandler->loadData(bitsArraySize);  // create file handler object

    
     // create hash functions according to the user input
     // copy the vector from the second element - from there the parameters are used for the hash functions
    std::vector<size_t> hashFunctionParams(numericParams.begin() + 1, numericParams.end());
    add_all_hash_functions(bloomFilterManager, hashFunctionParams);  // add the hash functions to the bloom filter
    
    ICommand* addCommand = new AddNewUrl(bloomFilterManager);
    ICommand* checkCommand = new CheckMatchUrl(bloomFilterManager);
    ICommand* deleteCommand = new DeleteUrl(bloomFilterManager);


    for (const auto& url : bloomFilterManager.getInsertedUrls()) {
        addCommand->execute(url.getUrl());  // add the urls to the bloom filter
    }
    

   
    //bloomFilterManager.setLoadingFlag(false); // set the loading flag to false after loading the data
    while (true) {
        std::getline(std::cin, line);

        if (line.empty()) {
            continue;
        }

        std::istringstream iss(line);
        std::string commandStr, urlStr, extra;
        iss >> commandStr >> urlStr >> extra;

        if (commandStr.empty() || urlStr.empty() || !extra.empty()) {
            continue;
        }

        CommandType command = CommandTranslator::parseCommandType(commandStr);

        switch (command) {
            case CommandType::POST: {
        
                addCommand->execute(urlStr);
                break;
            }
            case CommandType::GET: {
                checkCommand->execute(urlStr);
                break;
            }
            default:
                break;
        }

        // Save the data to the file after each command
        fileIoHandler->saveData(bloomFilterManager);
        
    }
}
