#include "gtest/gtest.h"
#include "../src/server/FileIOHandler.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/Url.h"
#include <fstream>
#include <filesystem>

class FileIOHandlerTest : public ::testing::Test {
protected:
    std::string testFilePath = "test_file.txt";
    std::string invalidFilePath = "invalid_file.txt";
    void SetUp() override {
        // Create a test file with valid data
        std::ofstream testFile(testFilePath);
        testFile << "http://example.com\n"; // URL 1
        testFile << "https://test.com\n"; // URL 2
        testFile.close();
    }

    void TearDown() override {
        // Remove the test file after each test
        std::remove(testFilePath.c_str());
        std::remove(invalidFilePath.c_str()); // Remove the invalid file if it exists
    }
};

// Test cases for FileIOHandler
TEST_F(FileIOHandlerTest, LoadData_ValidFile) {
FileIOHandler fileHandler(testFilePath);
size_t bitArraySize = 10; // Size of the bit array
BloomFilter bloomFilter = fileHandler.loadData(bitArraySize);
ASSERT_EQ(bloomFilter.getBitArray().size(), 10);

std::set<Url> urls = bloomFilter.getInsertedUrls();
ASSERT_EQ(urls.size(), 2);
// It's a set - check that count = how many times the url is in urls, is greater than 0
ASSERT_TRUE(urls.count(Url("http://example.com")) > 0);
ASSERT_TRUE(urls.count(Url("https://test.com")) > 0);
}


// Test duplicate URLs
TEST_F(FileIOHandlerTest, LoadData_DuplicateUrls) {
FileIOHandler fileHandler(testFilePath);
std::ofstream stdFile(testFilePath);
stdFile << "http://example.com\n"; // duplicate 1
stdFile << "https://test.com\n"; // duplicate 2
stdFile.close();

BloomFilter bloomFilter = fileHandler.loadData(10);
ASSERT_EQ(bloomFilter.getBitArray().size(), 10);
std::set<Url> urls = bloomFilter.getInsertedUrls();
ASSERT_EQ(urls.size(), 2); // checking no duplicates
// It's a set - check that count = how many times the url is in urls, is greater than 0
ASSERT_TRUE(urls.count(Url("http://example.com")) > 0);
ASSERT_TRUE(urls.count(Url("https://test.com")) > 0);

}

TEST_F(FileIOHandlerTest, SaveData_ValidData) {
FileIOHandler fileHandler(testFilePath);
BloomFilter bloomFilter(10);
// No hashing - that's not the point of this test
bloomFilter.addUrlManually(Url("http://example.com"));
bloomFilter.addUrlManually(Url("https://test.com"));
bloomFilter.addUrlManually(Url("http://examploid.com"));
bloomFilter.addUrlManually(Url("https://examplyada.com"));

fileHandler.saveData(bloomFilter);
    std::ifstream fileStream(testFilePath);


    std::set<Url> urlsFromFile;
    std::string line;

// Read all lines from the file and store them in the set
    while (std::getline(fileStream, line)) {
        urlsFromFile.insert(Url(line));  // Insert the Url object, not a pointer
    }

// Define the expected URLs (in set)
    std::set<Url> expectedUrls = {
        Url("http://example.com"),
        Url("https://test.com"),
        Url("http://examploid.com"),
        Url("https://examplyada.com")
};

// Assert that the sets are equal
ASSERT_EQ(urlsFromFile, expectedUrls);
}
