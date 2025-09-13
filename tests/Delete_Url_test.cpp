#include "gtest/gtest.h"
#include "../src/server/DeleteUrl.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/Url.h"

class DeleteUrlTest : public ::testing::Test {
protected:
    BloomFilter* bloomFilter;

    void SetUp() override {
        bloomFilter = new BloomFilter(10); // Initialize with 10 bits
    }

    void TearDown() override {
        delete bloomFilter;
    }
};

// Test case for the constructor of DeleteUrl, with string
TEST_F(DeleteUrlTest, Constructor) {
    DeleteUrl deleteUrl(*bloomFilter);
    EXPECT_NO_THROW(deleteUrl.execute("http://example.com"));
}

// Test case for the constructor of DeleteUrl, with Url object
TEST_F(DeleteUrlTest, ConstructorWithUrl) {
    Url url("http://example.com");
    DeleteUrl deleteUrl(*bloomFilter);
    EXPECT_NO_THROW(deleteUrl.execute(url.getUrl()));
}

// Test case for the execute method with a valid URL
TEST_F(DeleteUrlTest, ExecuteValidUrl) {
    DeleteUrl deleteUrl(*bloomFilter);
    std::string urlStr = "http://example.com";
    bloomFilter->addUrlManually(Url(urlStr)); // Add the URL to the Bloom filter
    EXPECT_TRUE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify URL is in the Bloom filter

    CommandStatus status = deleteUrl.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::URL_DELETED);
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify URL is deleted
}

// Test case for the execute method with a URL that is not in the BloomFilter
TEST_F(DeleteUrlTest, ExecuteUrlNotInBloomFilter) {
    DeleteUrl deleteUrl(*bloomFilter);
    std::string urlStr = "http://example.com";
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify URL is not in the Bloom filter

    CommandStatus status = deleteUrl.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::URL_FOR_DELETE_IS_NOT_EXISTS);
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify URL remains absent
}

//Test case for the execute method with a invalid Url, attempted to insert in the BloomFilter
TEST_F(DeleteUrlTest, ExecuteInvalidUrl) {
    DeleteUrl deleteUrl(*bloomFilter);
    std::string urlStr = "invalid_url";
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify invalid URL is not in the Bloom filter

    CommandStatus status = deleteUrl.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::INVALID_URL);
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify invalid URL remains absent
}

// Test case for the execute method with an empty URL
TEST_F(DeleteUrlTest, ExecuteEmptyUrl) {
    DeleteUrl deleteUrl(*bloomFilter);
    std::string urlStr = "";
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify empty URL is not in the Bloom filter

    CommandStatus status = deleteUrl.execute(urlStr);
    EXPECT_EQ(status, CommandStatus::INVALID_URL);
    EXPECT_FALSE(bloomFilter->isContainsUrl(Url(urlStr))); // Verify empty URL remains absent
}










// // Tests for the deleteUrlFromBlacklist method with a valid URL
// TEST_F(DeleteUrlTest, DeleteUrlFromBlacklistValid) {
//     DeleteUrl deleteUrl(*bloomFilter);
//     std::string url = "http://example.com";
//     bloomFilter->add_url_and_hash(url); // Add the URL to the BloomFilter
//     EXPECT_TRUE(bloomFilter->isContainsUrl(url)); // Check if the URL is in the BloomFilter (added successfully)

//     EXPECT_NO_THROW(deleteUrl.deleteUrlFromBlacklist(url)); // Check if deleteUrlFromBlacklist works without exceptions
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is deleted from the BloomFilter
// }

// // Test case for the deleteUrlFromBlacklist method with a URL that is not in the BloomFilter
// TEST_F(DeleteUrlTest, DeleteUrlFromBlacklistNotInBloomFilter) {
//     DeleteUrl deleteUrl(*bloomFilter);
//     std::string url = "http://example.com"; // URL not in the BloomFilter
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is not in the BloomFilter

//     EXPECT_NO_THROW(deleteUrl.deleteUrlFromBlacklist(url)); // Check if deleteUrlFromBlacklist works without exceptions
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is not in the BloomFilter (not added)
// }


// // Test case for the deleteUrlFromBlacklist method with an invalid URL
// TEST_F(DeleteUrlTest, DeleteUrlFromBlacklistInvalid) {
//     DeleteUrl deleteUrl(*bloomFilter);
//     std::string url = "invalid_url"; // Invalid URL format
//     bloomFilter->add_url_and_hash(url); // Add the URL to the BloomFilter
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is in the BloomFilter (added successfully)

//     EXPECT_NO_THROW(deleteUrl.deleteUrlFromBlacklist(url)); // Check if deleteUrlFromBlacklist works without exceptions
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is not in the BloomFilter (not added)
// }

// // Test case for the deleteUrlFromBlacklist method with an empty URL
// TEST_F(DeleteUrlTest, DeleteUrlFromBlacklistEmpty) {
//     DeleteUrl deleteUrl(*bloomFilter);
//     std::string url = ""; // Empty URL
//     EXPECT_NO_THROW(deleteUrl.deleteUrlFromBlacklist(url)); // Check if deleteUrlFromBlacklist works without exceptions
//     EXPECT_FALSE(bloomFilter->isContainsUrl(url)); // Check if the URL is not in the BloomFilter (not added)
// }

