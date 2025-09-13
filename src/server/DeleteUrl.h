#pragma once

#ifndef DELETE_URL
#define DELETE_URL
#include <iostream>

#include "ICommand.h"
#include "BloomFilter.h"  

class DeleteUrl : public ICommand {

    private:    
        BloomFilter& bloomFilter; // Reference to the BloomFilter object

    public:
        // Constractor
        DeleteUrl(BloomFilter& bloomFilterRef);

        // Function to execute the command, gets url as string return command status
        CommandStatus execute(const std::string& input) override;

        // Destructor
        ~DeleteUrl() = default; // Default destructor

};

#endif