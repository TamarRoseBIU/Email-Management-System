
// gets a few numbers from the user, and call the hash manager 
// bloomFilter.getHashManager().addHashFunction(new HashFunction(arg i)); - but right now getHashManager is const
// also possible - bloomFilter.addHashFunction(new HashFunction(arg i));

#include "BloomFilter.h"
#include "HashFunction.h"
#include <vector>

// Gets a vector of integers from the user - its length is the number of hash functions,
// and the values are the number of iterations for each hash function

void add_all_hash_functions(BloomFilter& bloomFilter, std::vector<size_t>& hashFunctionParams);

