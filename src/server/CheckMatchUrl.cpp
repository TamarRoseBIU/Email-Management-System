

#include <set>
#include <iostream>


#include "ICommand.h"
#include "BloomFilter.h"
#include "BitArray.h"  
#include "CheckMatchUrl.h"

#include "CheckMatchUrl.h"
#include "BitArray.h"




CheckMatchUrl::CheckMatchUrl(BloomFilter& bloomFilterRef)
    : bloomFilter(bloomFilterRef) {}

bool CheckMatchUrl::firstCheck(const std::string& url) {
    indexesSet = bloomFilter.from_url_to_indexes(url);

    BitArray& bitArray = bloomFilter.getBitArray();
   
    return bitArray.checkBits(indexesSet);
}

bool CheckMatchUrl::secondCheck(const std::string& url) const {
    return bloomFilter.isContainsUrl(url);
}

CommandStatus CheckMatchUrl::execute(const std::string& input) {

    if (!bloomFilter.isUrlValid(input)) {
        return CommandStatus::INVALID_URL; // URL is invalid
    }

    if (!firstCheck(input)) {
       // std::cout << "false1" << std::endl;
        return CommandStatus::CHECK_FALSE; // False
    } else {
       // std::cerr << "true1" << " ";
        if (secondCheck(input)) {
         //   std::cerr << "true2" << std::endl;
           return CommandStatus::CHECK_TRUE_TRUE; // True true
        } else {
        //    std::cerr << "false2" << std::endl;
            return CommandStatus::CHECK_TRUE_FALSE; // True false
        }
    }

    
}
