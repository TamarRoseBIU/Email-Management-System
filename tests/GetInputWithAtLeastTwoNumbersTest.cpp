#include <gtest/gtest.h>
#include <future>
#include <sstream>
#include <iostream>
#include <vector>
#include <string>
#include <thread>
#include <chrono>

// The function to be tested
std::vector<int> getInputWithAtLeastTwoNumbers();

// Utility to inject input into std::cin
void injectCin(const std::string& input) {
   static std::istringstream iss;
   iss.str(input);
   iss.clear();
   std::cin.rdbuf(iss.rdbuf());
}
// Test class
class GetInputWithAtLeastTwoNumbersTest : public ::testing::Test {
};


// Test: multiple invalid lines, then a valid one, should return correctly
TEST_F(GetInputWithAtLeastTwoNumbersTest, ValidInputAfterInvalidOnes) {
   injectCin("hello world\n42\n7 8\n");

   auto result = std::async(std::launch::async, [] {
       return getInputWithAtLeastTwoNumbers();
       });

   // ASSERT_EQ(result.wait_for(std::chrono::seconds(2)), std::future_status::ready);
   ASSERT_EQ(result.get(), std::vector<int>({ 7, 8 }));
}

