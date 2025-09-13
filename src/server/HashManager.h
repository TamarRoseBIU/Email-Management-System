#ifndef HASHMANAGER_H
#define HASHMANAGER_H

#include "IHashFunction.h"
#include <vector>

class HashManager {
private:
    // List to store different hash functions - type is the interface of hash function
    std::vector<IHashFunction*> hashFunctions;  

public:
    // Constructor
    HashManager();

    // Method to add a hash function to the manager hash function
    void addHashFunction(IHashFunction* hashFunction);

    // Method to hash a url using all stored hash functions and return the hash results 
    std::vector<std::size_t> hashUrl(const Url& url) const;

    // Get the total number of hash functions
    size_t getTotalHashFunctions() const;

};

#endif // HASHMANAGER_H
