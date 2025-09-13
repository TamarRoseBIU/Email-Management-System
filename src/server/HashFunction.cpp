#include "HashFunction.h"
#include "Url.h"
#include <functional>
#include <random>
#include <string>

std::function<size_t(const std::string&)> create_std_hash();
// std::function<size_t(const std::string&)> create_rand_hash();
// unsigned int generate_random();

// Constructor - initializes the hash function and number of iterations
HashFunction::HashFunction() {
	func = create_hash_function();
	num_of_iterations = 1; // Initialize the number of iterations to 1 - default value
}

// Constructor with parameters - initializes the hash function and number of iterations
HashFunction::HashFunction(int num) {
	func = create_hash_function();
	num_of_iterations = num; // Initialize the number of iterations
}

// Function to create a hash function - modifiable to use either create_std_hash or create_rand_hash
std::function<size_t(const std::string&)> HashFunction::create_hash_function() {
	return create_std_hash();
}

std::function<size_t(const std::string&)> HashFunction::getFunc() const {
	return func;
}

int HashFunction::getNumOfIterations() const {
		return num_of_iterations;
}

// Hash function implementation - hashes the URL using the hash function and number of iterations
std::size_t HashFunction::hash(const Url& url) const {
 	// Use the hash function to get the hash value
	std::size_t hash_value = func(url.getUrl());
	for (int i = 0; i < num_of_iterations - 1; i++) {
		 // convert size_t to string, to use func on it again
		hash_value = func(std::to_string(hash_value));
	}
	return hash_value;
}


// Function to create a standard hash function using std::hash
std::function<size_t(const std::string&)> create_std_hash() {
	return std::hash<std::string>{};
}

