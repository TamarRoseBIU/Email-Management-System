#include "gtest/gtest.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/Url.h"
#include "../src/server/HashManager.h"
#include "../src/server/BitArray.h"
#include "../src/server/HashFunction.h"
#include <set>

// Test fixture for BitArray
class BloomFilterTest : public ::testing::Test {
protected:
   BloomFilter* bloomFilter;

   void SetUp() override {
       bloomFilter = new BloomFilter(8); // Initialize with 8 bits
   }

   void TearDown() override {
       delete bloomFilter;
   }
};


// Test constructor
TEST_F(BloomFilterTest, Constructor) {
   EXPECT_EQ(bloomFilter->getBitArray().size(), 8); // Check size of bit array
   EXPECT_TRUE(bloomFilter->getInsertedUrls().empty()); // Check if inserted URLs are empty
   EXPECT_EQ(bloomFilter->getHashManager().getTotalHashFunctions(), 0); // Check if no hash functions are added

}

// Test addUrlManually
TEST_F(BloomFilterTest, addUrlManually) {
   Url url1("http://example.com");
   Url url2("http://examploid.org");

   bloomFilter->addUrlManually(url1);
   bloomFilter->addUrlManually(url2);

   EXPECT_EQ(bloomFilter->getInsertedUrls().size(), 2); // Check if two URLs are added

   std::set<Url> inserted_urls = {url1, url2};
   for (const auto& url : bloomFilter->getInsertedUrls()) {
       // Check if the inserted URLs are in the set
       EXPECT_TRUE(inserted_urls.find(url) !=  inserted_urls.end());
   }
   // If size == 2, and both urls in inserted_urls are in bloomFilter->getInsertedUrls()
   //-> it also means that no other url was added.

}

// Test isContainsUrl
TEST_F(BloomFilterTest, IsContainsUrl) {
   Url url1("http://example.com");
   Url url2("http://examploid.org");

   bloomFilter->addUrlManually(url1);
   bloomFilter->addUrlManually(url2);
   bloomFilter->addUrlManually(url1); // duplicates
   EXPECT_EQ(bloomFilter->getInsertedUrls().size(), 2); // Check if only two URLs are added

   EXPECT_TRUE(bloomFilter->isContainsUrl(url1)); // Check if url1 is in the Bloom Filter
   EXPECT_TRUE(bloomFilter->isContainsUrl(url2)); // Check if url2 is in the Bloom Filter

   Url url3("http://examplodion.com");
   EXPECT_FALSE(bloomFilter->isContainsUrl(url3)); // Check if a non-existent URL is not in the Bloom Filter
}

// Test clear
TEST_F(BloomFilterTest, clear_urls_and_bits) {
   Url url1("http://example.com");
   Url url2("http://examploid.org");

   bloomFilter->addUrlManually(url1);
   bloomFilter->addUrlManually(url2);

   // Assuming setBit is already true and tested
   bloomFilter->getBitArray().setBit(0, true); // Set the first bit to true
   bloomFilter->getBitArray().setBit(5, true); // Set the second bit to true

   bloomFilter->clear(); // Clear the Bloom Filter

   EXPECT_TRUE(bloomFilter->getInsertedUrls().empty()); // Check if inserted URLs are empty
   EXPECT_EQ(bloomFilter->getBitArray().size(), 8); // Check size of bit array

   for (std::size_t i = 0; i < bloomFilter->getBitArray().size(); ++i) {
       EXPECT_FALSE(bloomFilter->getBitArray().getBit(i)); // All bits should be false
   }
}

// Test BitArrayAsConst
TEST_F(BloomFilterTest, BitArrayAsConst) {
   EXPECT_EQ(bloomFilter->getBitArrayAsConst().size(), 8); // Check size of bit array
}


// Test addHashFunction
TEST_F(BloomFilterTest, AddHashFunction) {
   IHashFunction *hashFunction1 = new HashFunction(); // default - activated once
   IHashFunction *hashFunction2 = new HashFunction(); // activated once

   bloomFilter->addHashFunction(hashFunction1);
   bloomFilter->addHashFunction(hashFunction2);

   EXPECT_EQ(bloomFilter->getHashManager().getTotalHashFunctions(), 2); // Check if two hash functions are added

   // Checking sanity of hash functions -
   // Hash on a url, and compare it with Manager's hash result

   // This is the expected result of activating both hashes on the url
   Url url("http://example.com");
   size_t expected_hash1 = hashFunction1->hash(url);
   size_t expected_hash2 = hashFunction2->hash(url);

   std::vector<std::size_t> hashResults = bloomFilter->getHashManager().hashUrl(url);
   EXPECT_EQ(hashResults.size(), 2); // Check if two hash results are returned
   EXPECT_EQ(hashResults[0], expected_hash1); // Check if the first hash result is correct
   EXPECT_EQ(hashResults[1], expected_hash2); // Check if the second hash result is correct

   delete hashFunction1;
   delete hashFunction2;


}


// Test deleteUrl
TEST_F(BloomFilterTest, DeleteUrl) {
   Url url1("http://example.com");
   Url url2("http://examploid.org");
   Url url3("http://nonexistent.com");

   bloomFilter->addUrlManually(url1);
   bloomFilter->addUrlManually(url2);

   EXPECT_EQ(bloomFilter->getInsertedUrls().size(), 2); // Check if two URLs are added

   // Delete an existing URL
   bloomFilter->deleteUrl(url1);
   EXPECT_EQ(bloomFilter->getInsertedUrls().size(), 1); // Check if one URL is removed
   EXPECT_FALSE(bloomFilter->isContainsUrl(url1)); // Check if url1 is no longer in the Bloom Filter
   EXPECT_TRUE(bloomFilter->isContainsUrl(url2)); // Check if url2 is still in the Bloom Filter

   // Attempt to delete a non-existent URL
   bloomFilter->deleteUrl(url3);
   EXPECT_EQ(bloomFilter->getInsertedUrls().size(), 1); // Size should remain the same
   EXPECT_FALSE(bloomFilter->isContainsUrl(url3)); // url3 should not exist in the Bloom Filter

   // Delete the remaining URL
   bloomFilter->deleteUrl(url2);
   EXPECT_TRUE(bloomFilter->getInsertedUrls().empty()); // Check if all URLs are removed
}
