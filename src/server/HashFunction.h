#pragma once
#ifndef HASH_H
#define HASH_H
#include "IHashFunction.h"
#include "HashManager.h"
#include "Url.h"

#include <functional>
class HashFunction : public IHashFunction {
private:
	// Holds that hash function pointer
	std::function<size_t(const std::string&)> func;

	// Number of iterations for the hash function
	int num_of_iterations = 0;

public:
	// Default Constructor
	HashFunction();

	// Constructor with parameters
	HashFunction(int num);

	// Function to create a hash function
	std::function<size_t(const std::string&)> create_hash_function();

	// Hash function implementation
	std::size_t hash(const Url& url) const override;

	// Getter for func
	std::function<size_t(const std::string&)> getFunc() const;

	// Getter for num_of_iterations
	int getNumOfIterations() const;

};

#endif
