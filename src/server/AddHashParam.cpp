#include "BloomFilter.h"
#include "HashFunction.h"
#include <vector>

// Gets a vector of integers from the user - its length is the number of hash functions,
// and the values are the number of iterations for each hash function

void add_all_hash_functions(BloomFilter& bloomFilter, std::vector<size_t>& hashFunctionParams) {

    // or - hashManager = bloomFilter.getHashManager();
    // hashManager.addHashFunction(new HashFunction(hashFunctionParams[i])); - but right now getHashManager is const
    for(int i = 0; i < hashFunctionParams.size(); ++i) {
        // Create a new hash function with the current index and add it to the BloomFilter
        bloomFilter.addHashFunction(new HashFunction(hashFunctionParams[i]));  
    }
}