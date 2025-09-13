#include "Url.h"
#include "BloomFilter.h"
#include <regex>
#include <string>
#include <fstream>
#include <set>
#include <iostream>
#include <sstream>
#include "IHashFunction.h"
#include "HashManager.h"
#include "BitArray.h"
// #include "fromUrlToIndexes.h"


// Get a url, (assuming it's valid) hash it, and return the indexes to change
std::set<std::size_t> from_url_to_indexes(BloomFilter& bloom_filter, const Url& url) {

	// Hashing the url using the hash manager
	std::vector<std::size_t>  hashes = bloom_filter.getHashManager().hashUrl(url);

	// Set to store the calculated bit indices
	std::set<std::size_t> bit_indexes;

	// Compute each index by taking the hash modulo the size of the bit array
	std::size_t bit_array_size = bloom_filter.getBitArray().size();
	for (const auto& hash : hashes) {
		bit_indexes.insert(hash % bit_array_size);

	}

	return bit_indexes;
}
