#include "BloomFilter.h"
#include <iostream>
// Constructor - initializes the bit array with a given size and initializes the hash manager and inserted urls
BloomFilter::BloomFilter(std::size_t size)
    : bitArray(size), hashManager(), insertedUrls()/*, loadingFlag(true)*/ {
}

bool BloomFilter::isContainsUrl(const Url& url) const {
    return insertedUrls.find(url) != insertedUrls.end();
}

/*
void BloomFilter::setLoadingFlag(bool flag) {
	loadingFlag = flag; // Set the loading flag to false
}

bool BloomFilter::getLoadingFlag() const { return loadingFlag; } // Get the loading flag
*/

void BloomFilter::clear() {
    bitArray.clearAll();
    insertedUrls.clear();
}

// Returns a copy of the inserted urls
std::set<Url> BloomFilter::getInsertedUrls() const {
    return this->insertedUrls;
}

// Adds the url directly without hashing
void BloomFilter::addUrlManually(const Url& url) {
    insertedUrls.insert(url);  
}

// Returns a copy of HashManager
HashManager BloomFilter::getHashManager() const {
    return this->hashManager;
}

// Returns bit array, a reference in order to change it
BitArray& BloomFilter::getBitArray() {
    return this->bitArray;
}

// Returns bit array as const
BitArray BloomFilter::getBitArrayAsConst() const {
    return this->bitArray;
}

// Adds a new hash function to the manager
void BloomFilter::addHashFunction(IHashFunction* hashFunction) {
    hashManager.addHashFunction(hashFunction);  
}


// Get a url, (assuming it's valid) hash it, and return the indexes to change
std::set<std::size_t> BloomFilter::from_url_to_indexes(const Url& url) {

	// Hashing the url using the hash manager
	std::vector<std::size_t>  hashes = this->hashManager.hashUrl(url);
	

	// Set to store the calculated bit indices
	std::set<std::size_t> bit_indexes;


	// Compute each index by taking the hash modulo the size of the bit array
	std::size_t bit_array_size = this->bitArray.size();
	for (const auto& hash : hashes) {
		bit_indexes.insert(hash % bit_array_size);

	}
	

	return bit_indexes;
}

// Check if a URL is valid using regex
bool BloomFilter::isUrlValid(const Url& url) {
	//const std::regex pattern1(R"((http|https)://([a-zA-Z0-9.-]+)(:[0-9]{1,5})?(/.*)?)", std::regex::icase);
	//const std::regex pattern2(R"(www\.[a-zA-Z0-9-]+\.[a-zA-Z]{2,})", std::regex::icase);
	//return std::regex_match(url.getUrl(), pattern1) || std::regex_match(url.getUrl(), pattern2);

	const std::regex pattern(R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)");
	return std::regex_match(url.getUrl(), pattern);
}


/*
// Add a URL to the Bloom Filter, without hashing
bool BloomFilter::add_url_to_blacklist_and_valid(const Url& url) {
	if (!isUrlValid(url) || isContainsUrl(url)) {
		return false;
	}
	this->addUrlManually(url);
	return true;
}

// Add a URL and its hash to the Bloom Filter
void BloomFilter::add_url_and_hash(const Url& url) {
	

	// Add the URL to the Bloom filter's blacklist
	if (add_url_to_blacklist_and_valid(url) || this->loadingFlag) {
		// Get the hash-based bit indexes (as a set)
		std::set<std::size_t> indexes = from_url_to_indexes(url);

		// Set the corresponding bits in the bit array
		this->bitArray.turnsOnBits(indexes);
	}
}
	*/

// Delete a url from the url list
bool BloomFilter::deleteUrl(const Url& url){
	return insertedUrls.erase(url); 
	// returns the number of elements which were deleted from the set ->
	// returns 0 if no such Url existed, and 1 if existed ->
	// insertedUrls.erase(url) == 0 == false is no url existed
	// insertedUrls.erase(url) == 1 == if url existed
}

// Update the bit array according to the indexes received
void BloomFilter::updateBitArray(const Url& url) {
	std::set<std::size_t> indexes = this->from_url_to_indexes(url.getUrl());

	// Set the corresponding bits in the bit array
	this->getBitArray().turnsOnBits(indexes);
}