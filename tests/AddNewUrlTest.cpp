#include <gtest/gtest.h>
#include <set>
#include "../src/server/AddNewUrl.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/Url.h"
#include "../src/server/CommandTranslator.h"

class AddNewUrlTest : public ::testing::Test {};

TEST(AddNewUrlTest, ExecuteWithValidUrlSmallBloomFilter) {
    BloomFilter bloomFilter(10);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::string validUrlStr = "http://example.com";
    CommandStatus status = addNewUrlCommand.execute(validUrlStr);

    EXPECT_EQ(status, CommandStatus::URL_CREATED);
    EXPECT_TRUE(bloomFilter.isContainsUrl(Url(validUrlStr)));
}

// Check an empty url
TEST(AddNewUrlTest, ExecuteWithEmptyUrlSmallBloomFilter) {
    BloomFilter bloomFilter(10);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::string emptyUrlStr = "";
    CommandStatus status = addNewUrlCommand.execute(emptyUrlStr);

    EXPECT_EQ(status, CommandStatus::INVALID_URL);
    EXPECT_EQ(bloomFilter.getInsertedUrls().size(), 0);
}
// Check an invalid url
TEST(AddNewUrlTest, ExecuteWithInvalidUrlSmallBloomFilter) {
    BloomFilter bloomFilter(10);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::string invalidUrlStr = "ahjds0";
    CommandStatus status = addNewUrlCommand.execute(invalidUrlStr);

    EXPECT_EQ(status, CommandStatus::INVALID_URL);
    EXPECT_EQ(bloomFilter.getInsertedUrls().size(), 0);
}

TEST(AddNewUrlTest, ExecuteWithValidUrlLargeBloomFilter) {
    BloomFilter bloomFilter(1000);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::string validUrlStr = "http://example.com";
    CommandStatus status = addNewUrlCommand.execute(validUrlStr);

    EXPECT_EQ(status, CommandStatus::URL_CREATED);
    EXPECT_TRUE(bloomFilter.isContainsUrl(Url(validUrlStr)));
}

TEST(AddNewUrlTest, ExecuteWithMultipleUrls) {
    BloomFilter bloomFilter(100);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::set<std::string> urlStrs = {
        "http://example1.com",
        "http://example2.com",
        "http://example3.com"
    };

    for (const auto& urlStr : urlStrs) {
        CommandStatus status = addNewUrlCommand.execute(urlStr);
        EXPECT_EQ(status, CommandStatus::URL_CREATED);
    }

    for (const auto& urlStr : urlStrs) {
        EXPECT_TRUE(bloomFilter.isContainsUrl(Url(urlStr)));
    }
}

TEST(AddNewUrlTest, ExecuteWithMultipleUrlsAndDuplicates) {
    BloomFilter bloomFilter(100);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::vector<std::string> urlStrs = {
        "http://example1.com",
        "http://example2.com",
        "http://example1.com", // Duplicate URL
        "http://example3.com"
    };

    for (size_t i = 0; i < urlStrs.size(); ++i) {
        CommandStatus status = addNewUrlCommand.execute(urlStrs[i]);
        if (i != 2) { // Duplicate URL
            EXPECT_EQ(status, CommandStatus::URL_CREATED);
        }
    }

    EXPECT_EQ(bloomFilter.getInsertedUrls().size(), 3);
    EXPECT_TRUE(bloomFilter.isContainsUrl(Url("http://example1.com")));
    EXPECT_TRUE(bloomFilter.isContainsUrl(Url("http://example2.com")));
    EXPECT_TRUE(bloomFilter.isContainsUrl(Url("http://example3.com")));
}

TEST(AddNewUrlTest, ExecuteWithMultipleUrlsInLargeBloomFilter) {
    BloomFilter bloomFilter(1000);
    AddNewUrl addNewUrlCommand(bloomFilter);

    std::set<std::string> urlStrs = {
        "http://example1.com",
        "http://example2.com",
        "http://example3.com"
    };

    for (const auto& urlStr : urlStrs) {
        CommandStatus status = addNewUrlCommand.execute(urlStr);
        EXPECT_EQ(status, CommandStatus::URL_CREATED);
    }

    for (const auto& urlStr : urlStrs) {
        EXPECT_TRUE(bloomFilter.isContainsUrl(Url(urlStr)));
    }
}