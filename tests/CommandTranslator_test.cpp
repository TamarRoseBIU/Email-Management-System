
#include <gtest/gtest.h>
#include "../src/server/CommandTranslator.h"

class CommandTranslatorTest : public ::testing::Test {};

TEST_F(CommandTranslatorTest, ParseCommandType_ReturnsPostForPostInput) {
    EXPECT_EQ(CommandTranslator::parseCommandType("POST"), CommandType::POST);
}

TEST_F(CommandTranslatorTest, ParseCommandType_ReturnsGetForGetInput) {
    EXPECT_EQ(CommandTranslator::parseCommandType("GET"), CommandType::GET);
}

TEST_F(CommandTranslatorTest, ParseCommandType_ReturnsDeleteForDeleteInput) {
    EXPECT_EQ(CommandTranslator::parseCommandType("DELETE"), CommandType::DELETE);
}

TEST_F(CommandTranslatorTest, ParseCommandType_ReturnsInvalidForUnknownInput) {
    EXPECT_EQ(CommandTranslator::parseCommandType("PUT"), CommandType::INVALID);
    EXPECT_EQ(CommandTranslator::parseCommandType(""), CommandType::INVALID);
    EXPECT_EQ(CommandTranslator::parseCommandType("post"), CommandType::INVALID); // Case-sensitive
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForUrlCreated) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::URL_CREATED), "201 Created");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForUrlDeleted) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::URL_DELETED), "204 No Content");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForInvalidUrl) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::INVALID_URL), "400 Bad Request");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForCheckFalse) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::CHECK_FALSE), "200 Ok\n\n\nfalse");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForCheckTrueTrue) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::CHECK_TRUE_TRUE), "200 Ok\n\n\ntrue true");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForCheckTrueFalse) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::CHECK_TRUE_FALSE), "200 Ok\n\n\ntrue false");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForUrlAlreadyExists) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::URL_IS_ALREADY_EXISTS), "201 Created");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForUrlForDeleteNotExists) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::URL_FOR_DELETE_IS_NOT_EXISTS), "404 Not Found");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsCorrectStringForInvalidCommand) {
    EXPECT_EQ(CommandTranslator::statusToString(CommandStatus::INVALID_COMMAND), "400 Bad Request");
}

TEST_F(CommandTranslatorTest, StatusToString_ReturnsBadRequestForInvalidStatus) {
    // Cast an invalid enum value to CommandStatus
    CommandStatus invalidStatus = static_cast<CommandStatus>(999);
    EXPECT_EQ(CommandTranslator::statusToString(invalidStatus), "400 Bad Request");
}