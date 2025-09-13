#include "Manage_Command.h"
#include "CheckMatchUrl.h"
#include "AddNewUrl.h"
#include "DeleteUrl.h"
#include "AddHashParam.h"
#include "CommandTranslator.h"
#include "CommandsMenu.h"
#include <sstream>

// Constructor
Manage_Command::Manage_Command(BloomFilter& bloomFilter) 
    : commandsMenu(bloomFilter), // Initialize the command menu with the bloom filter
      translator() // Initialize the translator (default constructor)
{}

bool Manage_Command::parse_user_input(const std::string& input, std::string& commandStr, std::string& urlStr) {
    
    // Check for empty input first
    if (input.empty()) {
        return false;
    }

    std::istringstream iss(input);

    // Check if extraction of both tokens succeeded
    if (!(iss >> commandStr >> urlStr)) {
        return false;
    }
    
    // Check for additional input
    std::string extra;
    if (iss >> extra) {
        return false; // Return false if there's additional input
    }
    
    // Check if either token is empty
    if (commandStr.empty() || urlStr.empty()) {
        return false;
    }
    
    return true;
}

std::string Manage_Command::process_request(const std::string& user_input) {

    // 1st - parsing user input to two strings
    std::string commandStr;
    std::string urlStr;
    if (!parse_user_input(user_input, commandStr, urlStr)) {
        // Handle parsing error
        return translator.statusToString(CommandStatus::INVALID_COMMAND);
    }

    // 2nd - translating the user request to a command string syntax
    CommandType commandType = translator.parseCommandType(commandStr);
    if (commandType == CommandType::INVALID) {
        // Handle invalid command
        std::string response = translator.statusToString(CommandStatus::INVALID_COMMAND);
        return response;
    }


    // Convert the command string to an actual command type, that can be activated
    ICommand* command = commandsMenu.getCommand(commandType);
    
    // 3rd - converting the command string to an actual command type, that can be activated
    if (command == nullptr) {
        // Handle command not found
        std::string response = translator.statusToString(CommandStatus::INVALID_COMMAND);
        return response;
    }

    // 4th - executing the command and retrieving the finish status
    CommandStatus status = command->execute(urlStr);

    // 5th - translating the command's exit status into output to user
    std::string response = translator.statusToString(status);
    return response;

}


ICommand* Manage_Command::getPostCommandForInit() {
    CommandType type = CommandType::POST; // We want to get the POST command
    return commandsMenu.getCommand(type);
}