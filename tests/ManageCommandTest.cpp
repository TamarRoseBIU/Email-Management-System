#include <gtest/gtest.h>
#include "../src/server/Manage_Command.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/CommandTranslator.h"
#include "../src/server/Url.h"

// Test case structure for parameterized tests
struct ManageCommandTestCase {
    std::string input;
    std::string expectedOutput;
};

class ManageCommandTest : public ::testing::TestWithParam<ManageCommandTestCase> {
protected:
    BloomFilter* bloomFilter;
    Manage_Command* manager;

    void SetUp() override {
        bloomFilter = new BloomFilter(8); // Initialize with 8 bits
        manager = new Manage_Command(*bloomFilter);

        IHashFunction* hashFunction1 = new HashFunction(2); // Second hash function
        bloomFilter->addHashFunction(hashFunction1);
    }

    void TearDown() override {
        delete manager;
        delete bloomFilter;
    }
};

// Test parse_user_input for various input scenarios
TEST_F(ManageCommandTest, ParseUserInput_ValidInput) {
    std::string commandStr, urlStr;
    bool result = manager->parse_user_input("POST http://example.com", commandStr, urlStr);
    EXPECT_TRUE(result);
    EXPECT_EQ(commandStr, "POST");
    EXPECT_EQ(urlStr, "http://example.com");
}

TEST_F(ManageCommandTest, ParseUserInput_EmptyInput) {
    std::string commandStr, urlStr;
    bool result = manager->parse_user_input("", commandStr, urlStr);
    EXPECT_FALSE(result);
    EXPECT_TRUE(commandStr.empty());
    EXPECT_TRUE(urlStr.empty());
}

TEST_F(ManageCommandTest, ParseUserInput_MissingUrl) {
    std::string commandStr, urlStr;
    bool result = manager->parse_user_input("POST", commandStr, urlStr);
    EXPECT_FALSE(result);
}

TEST_F(ManageCommandTest, ParseUserInput_ExtraArguments) {
    std::string commandStr, urlStr;
    bool result = manager->parse_user_input("POST http://example.com extra", commandStr, urlStr);
    EXPECT_FALSE(result);
    EXPECT_EQ(commandStr, "POST");
    EXPECT_EQ(urlStr, "http://example.com");
}

TEST_F(ManageCommandTest, ParseUserInput_OnlyWhitespace) {
    std::string commandStr, urlStr;
    bool result = manager->parse_user_input("   ", commandStr, urlStr);
    EXPECT_FALSE(result);
}

// Parameterized test for process_request
TEST_P(ManageCommandTest, ProcessRequestTest) {
    const ManageCommandTestCase& tc = GetParam();
    std::string actual = manager->process_request(tc.input);
    EXPECT_EQ(actual, tc.expectedOutput);
}

// Test process_request with a sequence of operations (POST, GET, DELETE)
TEST_F(ManageCommandTest, ProcessRequest_SequenceOfOperations) {
    // Add a URL
    std::string result = manager->process_request("POST http://example.com");
    EXPECT_EQ(result, "201 Created");
    
    // Check if URL exists
    result = manager->process_request("GET http://example.com");
    EXPECT_EQ(result, "200 Ok\n\n\ntrue true");
    
    // Delete the URL
    result = manager->process_request("DELETE http://example.com");
    EXPECT_EQ(result, "204 No Content");
    
    // Verify URL is no longer in filter
    result = manager->process_request("GET http://example.com");
    EXPECT_EQ(result, "200 Ok\n\n\ntrue false");
}

// Test process_request with duplicate POST
TEST_F(ManageCommandTest, ProcessRequest_DuplicatePost) {
    // Add a URL
    std::string result = manager->process_request("POST http://example.com");
    EXPECT_EQ(result, "201 Created");
    
    // Try adding the same URL again
    result = manager->process_request("POST http://example.com");
    EXPECT_EQ(result, "201 Created"); // URL_IS_ALREADY_EXISTS maps to "201 Created"
}

// Test process_request with invalid URL
TEST_F(ManageCommandTest, ProcessRequest_InvalidUrl) {
    std::string result = manager->process_request("POST invalid_url");
    EXPECT_EQ(result, "400 Bad Request");
}


// Instantiate parameterized test suite
INSTANTIATE_TEST_SUITE_P(
    ValidAndInvalidInputs,
    ManageCommandTest,
    ::testing::Values(
        // Valid commands
        ManageCommandTestCase{"POST http://example.com", "201 Created"},
        ManageCommandTestCase{"GET http://example.com", "200 Ok\n\n\nfalse"}, // URL not added yet
        ManageCommandTestCase{"DELETE http://example.com", "404 Not Found"}, // URL not in filter
        // Invalid inputs
        ManageCommandTestCase{"POST", "400 Bad Request"}, // Missing URL
        ManageCommandTestCase{"unknownCommand http://example.com", "400 Bad Request"}, // Invalid command
        ManageCommandTestCase{"", "400 Bad Request"}, // Empty input
        ManageCommandTestCase{"POST http://example.com extra", "400 Bad Request"}, // Too many arguments
        ManageCommandTestCase{"post http://example.com", "400 Bad Request"}, // Case-sensitive command
        ManageCommandTestCase{"POST   ", "400 Bad Request"} // Empty URL
    )
);