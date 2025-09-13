#ifndef COMMANSMENU_H
#define COMMANSMENU_H

#include <memory>
#include <unordered_map>
#include "ICommand.h" 
#include "BloomFilter.h"

class CommandsMenu {
private:
    // A map that associates CommandType with its corresponding ICommand object
    std::unordered_map<CommandType, std::unique_ptr<ICommand>> commands;
public:
    // Constructor that initializes the commands map with command types and their corresponding ICommand objects
    CommandsMenu(BloomFilter& bloomFilter);

    // Function to get the command based on the CommandType
    ICommand* getCommand(CommandType type) const;

    // Destructor is not explicitly defined, as unique_ptr will automatically clean up the resources
    ~CommandsMenu() = default;


};

#endif // COMMANSMENU_H
