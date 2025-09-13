#pragma once
#ifndef CHECKMATCHURL_H
#define CHECKMATCHURL_H

#include <set>
#include <iostream>
#include "ICommand.h"
#include "BloomFilter.h"  
#include "BitArray.h"  


class CheckMatchUrl : public ICommand {
    BloomFilter& bloomFilter;   // Reference to the BloomFilter object
    std::set<std::size_t> indexesSet; // Set to store indexes of the URL (using from_url_to_indexes function)
   // bool checkBit; // Boolean to check if all the indexes in the set are 1 in the bit array (using check_bit function)
public:
    CheckMatchUrl (BloomFilter& bloomFilterRef);

    // Function to execute the command, gets url as string, return command status
    CommandStatus execute(const std::string& input) override;

    // Function to check if the hash result is in the bit array (the bits are 1)
    bool firstCheck(const std::string& url);

    // Function to check if the URL is in the blacklist- double check
    // This function should be called only if the first check passed (the bits are 1)
    bool secondCheck(const std::string& url) const;

    // Destructor
    ~CheckMatchUrl() = default; // Default destructor



};
#endif // CHECKMATCHURL_H
