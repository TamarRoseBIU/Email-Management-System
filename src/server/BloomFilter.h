#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H

#include "BitArray.h"
#include "HashManager.h"
#include "Url.h"
#include <set> 
#include <regex>

class BloomFilter {

private:
    BitArray bitArray;
    HashManager hashManager;
    std::set<Url> insertedUrls;  // The list of inserted URLs
   // bool loadingFlag;

    

public:
    bool isUrlValid(const Url& url);
    // Constructor that allows initializing with a size for the bit array
    BloomFilter(std::size_t size);

  //  void setLoadingFlag(bool flag); // Set the loading flag to false
   // bool getLoadingFlag() const; // Get the loading flag

    // Checks if a specific url is in the Bloom Filter 
    bool isContainsUrl(const Url& url) const;

    // Clears the bit array and the list of inserted URLs
    void clear();

    // Returns a copy of the inserted URLs
    std::set<Url> getInsertedUrls() const;

    // Manually adds a url to the inserted list 
    void addUrlManually(const Url& url);

    // Returns a copy of the HashManager
    HashManager getHashManager() const;

    // Returns bit array
    BitArray& getBitArray();

    // Returns bit array as const
    BitArray getBitArrayAsConst() const;

    // Adds a hash function to the manager
    void addHashFunction(IHashFunction* hashFunction);

    // Option to add functions that change the bit array and activate the hash with hash manager class
    // Get a url, hash it, and return the indexes to change
    std::set<std::size_t> from_url_to_indexes(const Url& url);

    // Add url to the Bloom Filter, check validity and add without hashing
    bool add_url_to_blacklist_and_valid(const Url& url);

    // Add a URL and its hash to the Bloom Filter
    void add_url_and_hash(const Url& url);

    // Delete a url from the url list. return true if the url existed, and false if not
    bool deleteUrl(const Url& url);

    // Update the bit array according to the url
    void updateBitArray(const Url& url);
};

#endif // BLOOMFILTER_H

