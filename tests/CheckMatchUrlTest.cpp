// #include <gtest/gtest.h>
// #include "../src/CheckMatchUrl.h"
// #include "../src/BloomFilter.h"
// #include "../src/IHashFunction.h"
// #include "../src/HashFunction.h"
// class CheckMatchUrlTest : public ::testing::Test {

// };


// // Test when URL was never added and is not in the Bloom Filter - bit array: all bits are false
// TEST(CheckMatchUrlTest, UrlNotInFilter_ReturnsFalse) {
//    BloomFilter bloomFilter(128);
//    IHashFunction *hashFunction1 = new HashFunction(); // default - activated once
//    IHashFunction *hashFunction2 = new HashFunction(2); // activated once

//    bloomFilter.addHashFunction(hashFunction1);
//    bloomFilter.addHashFunction(hashFunction2);


//    CheckMatchUrl checker(bloomFilter);
//    //Url url("http://notinserted.com");
//    Url url("www.example.com0");
//    EXPECT_FALSE(checker.firstCheck(url.getUrl()));
// }

// // Test when it needs to add an url - bit array: all bits are true
// TEST(CheckMatchUrlTest, UrlNotInFilter_ReturnsTrue) {
//    BloomFilter bloomFilter(128);
//    IHashFunction *hashFunction1 = new HashFunction(); // default - activated once
//    IHashFunction *hashFunction2 = new HashFunction(2); // activated once

//    bloomFilter.addHashFunction(hashFunction1);
//    bloomFilter.addHashFunction(hashFunction2);

//    bloomFilter.getBitArray().TurnsAllBits();
//    CheckMatchUrl checker(bloomFilter);
//    Url url("http://notinserted.com");
//    EXPECT_TRUE(checker.firstCheck(url.getUrl()));
// }

// // Test when contains url - second check needs to return true
// TEST(CheckMatchUrlTest, UrlNotInFilterSecondCheck_ReturnsTrue) {
//    BloomFilter bloomFilter(128);
//    IHashFunction *hashFunction1 = new HashFunction(); // default - activated once
//    IHashFunction *hashFunction2 = new HashFunction(2); // activated once

//    bloomFilter.addHashFunction(hashFunction1);
//    bloomFilter.addHashFunction(hashFunction2);
//    CheckMatchUrl checker(bloomFilter);
//    Url url("http://notinserted.com");
//    bloomFilter.addUrlManually(url);
//    EXPECT_TRUE(checker.secondCheck(url.getUrl()));
// }

// // Test when doesn't contain url - second check needs to return false
// TEST(CheckMatchUrlTest, UrlNotInFilterSecondCheck_ReturnsFalse) {
//    BloomFilter bloomFilter(128);
//    IHashFunction *hashFunction1 = new HashFunction(); // default - activated once
//    IHashFunction *hashFunction2 = new HashFunction(2); // activated once

//    bloomFilter.addHashFunction(hashFunction1);
//    bloomFilter.addHashFunction(hashFunction2);
//    CheckMatchUrl checker(bloomFilter);
//    Url url("http://notinserted.com");
//    EXPECT_FALSE(checker.secondCheck(url.getUrl()));
// }
#include <gtest/gtest.h>
#include "../src/server/CheckMatchUrl.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/IHashFunction.h"
#include "../src/server/HashFunction.h"
#include "../src/server/Url.h"
#include "../src/server/AddNewUrl.h"

class CheckMatchUrlTest : public ::testing::Test {};

TEST(CheckMatchUrlTest, UrlNotInFilter_ReturnsCheckFalse) {
    BloomFilter bloomFilter(128);
    IHashFunction* hashFunction1 = new HashFunction(); // Default hash function
    IHashFunction* hashFunction2 = new HashFunction(2); // Second hash function

    bloomFilter.addHashFunction(hashFunction1);
    bloomFilter.addHashFunction(hashFunction2);

    CheckMatchUrl checker(bloomFilter);
    std::string urlStr = "http://example.com";

    CommandStatus status = checker.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::CHECK_FALSE);
    EXPECT_FALSE(checker.firstCheck(urlStr));
    //EXPECT_FALSE(checker.secondCheck(urlStr));
}

TEST(CheckMatchUrlTest, AllBitsTrue_ReturnsCheckTrueFalse) {
    BloomFilter bloomFilter(128);
    IHashFunction* hashFunction1 = new HashFunction(); // Default hash function
    IHashFunction* hashFunction2 = new HashFunction(2); // Second hash function

    bloomFilter.addHashFunction(hashFunction1);
    bloomFilter.addHashFunction(hashFunction2);

    bloomFilter.getBitArray().TurnsAllBits(); // Set all bits to true
    CheckMatchUrl checker(bloomFilter);
    std::string urlStr = "http://example.com";

    CommandStatus status = checker.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::CHECK_TRUE_FALSE);
    EXPECT_TRUE(checker.firstCheck(urlStr));
    //EXPECT_FALSE(checker.secondCheck(urlStr));
}

TEST(CheckMatchUrlTest, UrlInFilterSecondCheck_ReturnsCheckTrueTrue) {
   BloomFilter bloomFilter(128);
   IHashFunction* hashFunction1 = new HashFunction(); // Default hash function
   IHashFunction* hashFunction2 = new HashFunction(2); // Second hash function

   bloomFilter.addHashFunction(hashFunction1);
   bloomFilter.addHashFunction(hashFunction2);

   CheckMatchUrl checker(bloomFilter);
   std::string urlStr = "http://example.com";
   //bloomFilter.addUrlManually(Url(urlStr)); // Add URL to Bloom filter
   //bloomFilter.add_url_and_hash(Url(urlStr));
   AddNewUrl addNewUrlCommand(bloomFilter);
   addNewUrlCommand.execute(urlStr);

   CommandStatus status = checker.execute(urlStr);
   EXPECT_EQ(status, CommandStatus::CHECK_TRUE_TRUE);
   //EXPECT_TRUE(checker.firstCheck(urlStr));
   EXPECT_TRUE(checker.secondCheck(urlStr));
}

TEST(CheckMatchUrlTest, UrlNotInFilterSecondCheck_ReturnsCheckFalse) {
    BloomFilter bloomFilter(128);
    IHashFunction* hashFunction1 = new HashFunction(); // Default hash function
    IHashFunction* hashFunction2 = new HashFunction(2); // Second hash function

    bloomFilter.addHashFunction(hashFunction1);
    bloomFilter.addHashFunction(hashFunction2);

    CheckMatchUrl checker(bloomFilter);
    std::string urlStr = "http://example.com";

    CommandStatus status = checker.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::CHECK_FALSE);
    //EXPECT_FALSE(checker.firstCheck(urlStr));
    EXPECT_FALSE(checker.secondCheck(urlStr));
}