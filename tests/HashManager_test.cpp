#include "gtest/gtest.h"
#include "../src/server/HashManager.h"
#include "../src/server/HashFunction.h"
#include "../src/server/IHashFunction.h"

// Test fixture for BitArray
class HashManagerTest : public ::testing::Test {
protected:
   HashManager* hashManager; ;

   void SetUp() override {
       hashManager = new HashManager();
   }

   void TearDown() override {
       delete hashManager;
   }
};


// Test constructor and size
TEST_F(HashManagerTest, ConstructorAndSize) {
   EXPECT_EQ(hashManager->getTotalHashFunctions(), 0);
}


// Test addHashFunction and getTotalHashFunctions
TEST_F(HashManagerTest, addHashFunction_getTotalHashFunctions) {
   IHashFunction* hashFunction1 = new HashFunction(); // Assuming HashFunction is already tested
   IHashFunction* hashFunction2 = new HashFunction();
   hashManager->addHashFunction(hashFunction1);
   hashManager->addHashFunction(hashFunction2);
   EXPECT_EQ(hashManager->getTotalHashFunctions(), 2);
}

// Test hashUrl with one hash function
TEST_F(HashManagerTest, HashUrl) {
   IHashFunction* hashFunction1 = new HashFunction(); // A hash function with default constructor - activated once
   IHashFunction* hashFunction2 = new HashFunction(2); // A hash function with 2 iterations - activated twice
   hashManager->addHashFunction(hashFunction1);
   hashManager->addHashFunction(hashFunction2);

   Url url("http://example.com");
   std::vector<std::size_t> hash_result = hashManager->hashUrl(url);
   EXPECT_EQ(hash_result.size(), 2);

   // check that the hashing output of the functions is the same, since there's no getHashFunction in the interface

   std::size_t expected_hash1 = hashFunction1->hash(url);
   std::size_t expected_hash2 = hashFunction2->hash(url);

   EXPECT_EQ(hash_result[0], expected_hash1); // Check if the first hash result is correct
   EXPECT_EQ(hash_result[1], expected_hash2); // Check if the second hash result is correct

   delete hashFunction1;
   delete hashFunction2;
}
