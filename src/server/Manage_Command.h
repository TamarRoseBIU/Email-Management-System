#pragma once
#ifndef MANAGE_H
#define MANAGE_H
#include "IIOHandler.h"
#include "FileIOHandler.h"
#include "AddHashParam.h"
#include "ICommand.h"
#include "BloomFilter.h"  
#include "CommandTranslator.h"
#include "CommandsMenu.h"


class Manage_Command {
    private:
        CommandsMenu commandsMenu;
        CommandTranslator translator;
    
    public:
        // Ctor
        Manage_Command(BloomFilter& bloomFilterRef); 

        // Processes a given request by activating the corresponding operation on the BloomFilter, using the provided URL string
        std::string process_request(const std::string& user_input); 

        // Function to get the Post command pointer for initialization
        ICommand* getPostCommandForInit();

        // Parsing the user input to 2 separate strings
        bool parse_user_input(const std::string& input, std::string& command, std::string& urlStr);

        
};

#endif