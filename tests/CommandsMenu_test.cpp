#include "../src/server/CommandsMenu.h"

#include <gtest/gtest.h>
#include "../src/server/BloomFilter.h"
#include "../src/server/AddNewUrl.h"
#include "../src/server/CheckMatchUrl.h"
#include "../src/server/DeleteUrl.h"

class CommandsMenuTest : public ::testing::Test {
protected:
    BloomFilter* bloomFilter;

    void SetUp() override {
        bloomFilter = new BloomFilter(128); // Initialize with 128 bits
    }

    void TearDown() override {
        delete bloomFilter;
    }
};

TEST_F(CommandsMenuTest, ConstructorInitializesCommandsCorrectly) {
    CommandsMenu menu(*bloomFilter);

    // Verify that getCommand returns non-null pointers for valid command types
    EXPECT_NE(menu.getCommand(CommandType::POST), nullptr);
    EXPECT_NE(menu.getCommand(CommandType::GET), nullptr);
    EXPECT_NE(menu.getCommand(CommandType::DELETE), nullptr);

    // Verify the types of the commands
    EXPECT_TRUE(dynamic_cast<AddNewUrl*>(menu.getCommand(CommandType::POST)) != nullptr);
    EXPECT_TRUE(dynamic_cast<CheckMatchUrl*>(menu.getCommand(CommandType::GET)) != nullptr);
    EXPECT_TRUE(dynamic_cast<DeleteUrl*>(menu.getCommand(CommandType::DELETE)) != nullptr);
}

TEST_F(CommandsMenuTest, GetCommandReturnsCorrectCommandForPost) {
    CommandsMenu menu(*bloomFilter);
    ICommand* command = menu.getCommand(CommandType::POST);

    ASSERT_NE(command, nullptr);
    EXPECT_TRUE(dynamic_cast<AddNewUrl*>(command) != nullptr);
}

TEST_F(CommandsMenuTest, GetCommandReturnsCorrectCommandForGet) {
    CommandsMenu menu(*bloomFilter);
    ICommand* command = menu.getCommand(CommandType::GET);

    ASSERT_NE(command, nullptr);
    EXPECT_TRUE(dynamic_cast<CheckMatchUrl*>(command) != nullptr);
}

TEST_F(CommandsMenuTest, GetCommandReturnsCorrectCommandForDelete) {
    CommandsMenu menu(*bloomFilter);
    ICommand* command = menu.getCommand(CommandType::DELETE);

    ASSERT_NE(command, nullptr);
    EXPECT_TRUE(dynamic_cast<DeleteUrl*>(command) != nullptr);
}

TEST_F(CommandsMenuTest, GetCommandReturnsNullForInvalidCommandType) {
    CommandsMenu menu(*bloomFilter);
    ICommand* command = menu.getCommand(CommandType::INVALID); // Assuming INVALID is a valid CommandType enum value

    EXPECT_EQ(command, nullptr);
}