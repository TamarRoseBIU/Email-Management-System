
#include <gtest/gtest.h>
#include "../src/server/HashFunction.h"
#include "../src/server/Url.h"

// Check that different hashes return the same value on the same url
TEST(HashFunctionTest, HashReturnsConsistentResult) {
   HashFunction hashFunc;
   Url url("https://example.com");

   size_t h1 = hashFunc.hash(url);
   size_t h2 = hashFunc.hash(url);

   EXPECT_EQ(h1, h2);
}

// Check that the hashes are different for different URLs
TEST(HashFunctionTest, DifferentUrlsReturnDifferentHashes) {
   HashFunction hashFunc;
   Url url1("https://example1.com");
   Url url2("https://example2.com");

   size_t h1 = hashFunc.hash(url1);
   size_t h2 = hashFunc.hash(url2);

   EXPECT_NE(h1, h2); 
}

// checking several itertions
TEST(HashFunctionTest, HashWithMultipleIterations) {
   HashFunction hashFunc(3); // 3 itertions
   Url url("https://example.com");

   size_t h = hashFunc.hash(url);

   // We don't check a spesific value, only check it is a valid operation and don't crush
   EXPECT_GT(h, 0);
}

