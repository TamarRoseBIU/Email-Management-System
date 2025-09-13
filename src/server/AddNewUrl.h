#pragma once


#include <iostream>

#include "ICommand.h"
#include "BloomFilter.h"  

class AddNewUrl : public ICommand {
    BloomFilter& bloomFilter; // Reference to the BloomFilter object
public:


    // Constructor
    AddNewUrl(BloomFilter& bloomFilterRef);

    // Function to execute the command, gets url as string return command status
    CommandStatus execute(const std::string& input) override;

    // Destructor
    ~AddNewUrl() = default; // Default destructor


};

