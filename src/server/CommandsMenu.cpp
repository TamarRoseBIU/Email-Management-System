#include "CommandsMenu.h"
#include "AddNewUrl.h"
#include "CheckMatchUrl.h"
#include "DeleteUrl.h"

// Constructor that initializes the commands map with command types and their corresponding ICommand objects
CommandsMenu::CommandsMenu(BloomFilter& bloomFilter) {
    commands[CommandType::POST] = std::make_unique<AddNewUrl>(bloomFilter);
    commands[CommandType::GET] = std::make_unique<CheckMatchUrl>(bloomFilter);
    commands[CommandType::DELETE] = std::make_unique<DeleteUrl>(bloomFilter);
}

ICommand* CommandsMenu::getCommand(CommandType type) const {
    // Use find to check if the command type exists in the map
    auto it = commands.find(type);
    // If the command type is found, return the corresponding ICommand pointer
    if (it != commands.end()) {
        return it->second.get();
    }
    // If the command type is not found, return nullptr or handle the error as needed
    return nullptr;
}
