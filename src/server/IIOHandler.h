#ifndef IIOHANDLER_H
#define IIOHANDLER_H

#include <set>
#include "Url.h"
#include "BloomFilter.h"

class IIOHandler {
public:
    virtual ~IIOHandler() = default;

    // Load data (bit array and urls list) from input source 
    virtual BloomFilter loadData(std::size_t bitArraySize) = 0;

    // Save data to output destination
    virtual void saveData(const BloomFilter& bloomFilterObj) = 0;

};

#endif // IIOHANDLER_H

