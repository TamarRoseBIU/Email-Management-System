#include "gtest/gtest.h"
#include "../src/server/BloomFilter.h"
#include <fstream>
#include <cstdio>

// Test fixture for BloomFilter tests
class AppTest : public ::testing::Test {
protected:
    const std::string filename = "test_filter.bin";

    // Cleanup after each test – remove saved file
    void TearDown() override {
        std::remove(filename.c_str());
    }

    // Helper to create BloomFilter instance
    BloomFilter createFilter(int size) {
        return BloomFilter(size);
    }

    // Helper to create a URL object
    Url createUrl(const std::string& urlString) {
        return Url(urlString);
    }
};

// Test: Check if a valid URL is detected after adding it manually
TEST_F(AppTest, AddUrlManually) {
    auto bloomFilter = createFilter(10);
    Url url = createUrl("http://validurl.com");

    bloomFilter.addUrlManually(url);
   
    EXPECT_TRUE(bloomFilter.isContainsUrl(url));  // Check that the URL is contained in the filter
}

// Test: Check if a URL is already contained (doesn't allow duplicates)
TEST_F(AppTest, PreventDuplicateUrls) {
    auto bloomFilter = createFilter(10);
    Url url = createUrl("http://example.com");

    bloomFilter.add_url_and_hash(url);
    bloomFilter.add_url_and_hash(url);  // Add again, should not duplicate

    EXPECT_TRUE(bloomFilter.isContainsUrl(url));  // Check that the URL is still contained in the filter
}

// Test: Ensure that a URL is not present if it was not added
TEST_F(AppTest, UrlNotExist) {
    auto bloomFilter = createFilter(10);
    Url url = createUrl("http://notaddedurl.com");

    EXPECT_FALSE(bloomFilter.isContainsUrl(url));  // Expecting false since URL was not added
}

// Test: Ensure false positive behavior for a URL that is not in the filter
TEST_F(AppTest, FalsePositiveTest) {
    auto bloomFilter = createFilter(10);
    Url url1 = createUrl("http://example.com");
    Url url2 = createUrl("http://fakeurl.com");

    bloomFilter.add_url_and_hash(url1);

    // A false positive can happen, so the check should be that it might be found
    EXPECT_TRUE(bloomFilter.isContainsUrl(url1));
    EXPECT_FALSE(bloomFilter.isContainsUrl(url2));  // URL2 should not be found
}

// Test: Test URL validity check
TEST_F(AppTest, UrlValidation) {
    auto bloomFilter = createFilter(10);
    Url validUrl = createUrl("http://validurl.com");
    Url invalidUrl1 = createUrl("bad input");
    Url invalidUrl2 = createUrl("http//missing-colon.com");

    EXPECT_TRUE(bloomFilter.isUrlValid(validUrl));
    EXPECT_FALSE(bloomFilter.isUrlValid(invalidUrl1));
    EXPECT_FALSE(bloomFilter.isUrlValid(invalidUrl2));
}

// Test: Ensure that multiple URLs can be added and queried correctly
TEST_F(AppTest, AddMultipleUrls) {
    auto bloomFilter = createFilter(64);
    Url url1 = createUrl("http://a.com");
    Url url2 = createUrl("http://b.com");
    Url url3 = createUrl("http://c.com");

    bloomFilter.add_url_and_hash(url1);
    bloomFilter.add_url_and_hash(url2);
    bloomFilter.add_url_and_hash(url3);

    EXPECT_TRUE(bloomFilter.isContainsUrl(url1));
    EXPECT_TRUE(bloomFilter.isContainsUrl(url2));
    EXPECT_TRUE(bloomFilter.isContainsUrl(url3));

    Url url4 = createUrl("http://notaddedurl.com");
    EXPECT_FALSE(bloomFilter.isContainsUrl(url4));  // This URL was not added
}

// Test: Ensure that the Bloom Filter's clear method clears all data
TEST_F(AppTest, ClearTest) {
    auto bloomFilter = createFilter(10);
    Url url = createUrl("http://example.com");

    bloomFilter.add_url_and_hash(url);
    EXPECT_TRUE(bloomFilter.isContainsUrl(url));  // URL should exist before clear

    bloomFilter.clear();
    EXPECT_FALSE(bloomFilter.isContainsUrl(url));  // URL should not exist after clear
}

// Test: Ensure that the Bloom Filter's loading flag behaves correctly
TEST_F(AppTest, LoadingFlagTest) {
    auto bloomFilter = createFilter(10);
    EXPECT_TRUE(bloomFilter.getLoadingFlag());  // By default, the flag should be true

    bloomFilter.setLoadingFlag(false);
    EXPECT_FALSE(bloomFilter.getLoadingFlag());  // Flag should be false after setting
}
