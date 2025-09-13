#include "HashManager.h"
#include <stdexcept>

// Constructor - initializes an empty list of hash functions
HashManager::HashManager() {}

// Adds a hash function to the list of hash functions
void HashManager::addHashFunction(IHashFunction* hashFunction) {
    if (hashFunction == nullptr) {
        throw std::invalid_argument("Cannot add a null hash function");
    }
    hashFunctions.push_back(hashFunction);
}

// Hashes a url using all stored hash functions and returns the results
std::vector<std::size_t> HashManager::hashUrl(const Url& url) const {
    std::vector<std::size_t> hashes;
    for (const IHashFunction* hashFunction : hashFunctions) 
    {
        hashes.push_back(hashFunction->hash(url));
    }
    return hashes;
}

// Return the number of hash functions
size_t HashManager::getTotalHashFunctions() const {
    return hashFunctions.size();  
}