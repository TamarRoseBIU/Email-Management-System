
#include <iostream>
#include <regex>
#include <string>
#include <fstream>
#include <functional>
#include <vector>
#include <random>

#include "Url.h"
#include "IHashFunction.h"
#include "HashManager.h"
#include "BitArray.h"
#include "BloomFilter.h"
#include "HashFunction.h"
#include <iostream>
#include "ICommand.h"
#include "AddNewUrl.h"


AddNewUrl::AddNewUrl(BloomFilter& bloomFilterRef)
    : bloomFilter(bloomFilterRef) {}

CommandStatus AddNewUrl::execute(const std::string& input) {

    Url url(input); // Create a Url object from the input string


    // Check if the URL is valid using regex
    if (!bloomFilter.isUrlValid(url)) {
		return CommandStatus::INVALID_URL;
	}

    // Check if the URL is already in the Bloom filter
   // if (bloomFilter.isContainsUrl(url)) {
   //     return CommandStatus::URL_IS_ALREADY_EXISTS; // URL is already in the filter
  //  }

    // Add the URL to the Bloom filter
    bloomFilter.addUrlManually(url);

    // Change the bit array

    bloomFilter.updateBitArray(url);
    // Get the hash-based bit indexes (as a set)
	//std::set<std::size_t> indexes = bloomFilter.from_url_to_indexes(url);

	// Set the corresponding bits in the bit array
	//bloomFilter.getBitArray().turnsOnBits(indexes);

    return CommandStatus::URL_CREATED; // URL is valid and added to the filter
}


