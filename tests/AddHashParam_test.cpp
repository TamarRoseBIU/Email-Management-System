#include "gtest/gtest.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/HashFunction.h"
#include "../src/server/AddHashParam.h"
#include <vector>


// Test that hash functions are added correctly to the BloomFilter
TEST(AddHashParamTest, AddMultipleHashFunctions) {
   BloomFilter bloomFilter(8); // Initialize with 8 bits
   std::vector<size_t> hashFunctionParams = {3, 5, 7};
   add_all_hash_functions(bloomFilter, hashFunctionParams);
   // Verify that the correct number of hash functions were added
   ASSERT_EQ(bloomFilter.getHashManager().getTotalHashFunctions(), hashFunctionParams.size());
}

// Test with a single hash function parameter
TEST(AddHashParamTest, AddSingleHashFunction) {
   BloomFilter bloomFilter(8); // Initialize with 8 bits
   std::vector<size_t> hashFunctionParams = {10};

   add_all_hash_functions(bloomFilter, hashFunctionParams);

   // Verify that one hash function was added
   ASSERT_EQ(bloomFilter.getHashManager().getTotalHashFunctions(), 1);
}
