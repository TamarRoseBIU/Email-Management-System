#include <iostream>
#include <sstream>
#include <vector>
#include <string>

//using namespace std;


// Function to get input from the user and validate it
std::vector<int> getInputWithAtLeastTwoNumbers() {
    std::string line;
    std::vector<int> numbers;

    // Prompt the user for input until valid input is received
    while (true) {
        std::getline(std::cin, line);

        std::istringstream iss(line);
        int num;
        // Clear the vector to store new input
        numbers.clear();

        bool invalidInput = false;
        std::string token;
        while (iss >> token) {
            std::istringstream tokenStream(token);
            // Check if the token can be converted to an integer (only numbers)
            // and if the are more tokens in the stream
            if (tokenStream >> num && tokenStream.eof()) {
                // Check if the numbers are positive
                if (num > 0) {
                    // Add the number to the vector if it's valid
                    numbers.push_back(num);
                } else {
                    invalidInput = true; // Number is zero or negative
                    break;
                }
            } else {
                invalidInput = true;
                break;
            }
        }

        // Check if the input is valid (contains only numbers) and contains at least two numbers
        // if not, prompt the user again
        if (invalidInput || numbers.size() < 2) {
                continue;
        }

        break;
    }

    return numbers;
}
