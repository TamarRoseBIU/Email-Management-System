#include "BitArray.h"
#include <algorithm>
#include <iostream>

// Constructor initializes the vector with false to all bits
BitArray::BitArray(std::size_t size) : bits(size, false) {}

// Set a bit at a given index to a given value
void BitArray::setBit(std::size_t index, bool value) {
    // bit in range
    if (index < bits.size()) {
        bits[index] = value;
    }
}

void BitArray::printBits() {
    for (size_t i = 0; i < bits.size(); i++) {
        std::cout << bits[i] << " ";// Print each bit followed by a space
    }
    std::cout << std::endl; // Print a newline after all bits are printed
    
}

// Turns on the bits of the given indexes
void BitArray::turnsOnBits(std::set<std::size_t> indexes) {
    for (std::size_t index : indexes) {
        setBit(index, true);
    }
}

// Turns on all bits
void BitArray::TurnsAllBits() {
    for (size_t i = 0; i < bits.size(); i++) {
        setBit(i, true);
    }
}

// Checks if all the bits received are on 
bool BitArray::checkBits(std::set<std::size_t> indexes) const {
    for (int index : indexes) {
        if (!bits[index]) {
            return false;
        }
    }
    return true;
}

// Return the value of the bit at a given index
bool BitArray::getBit(std::size_t index) const {
    // bit in range
    if (index < bits.size()) {
        return bits[index];
    }
    // option to error msg
    return NULL;
}

// Checks if all bits are on
bool BitArray::areAllBitsOn() const {
    for (std::size_t i = 0; i < bits.size(); ++i) {
        if (!bits[i]) {
            return false;
        }
    }
    return true;
}

// Clear all bits - set them to false
void BitArray::clearAll() {
    std::fill(bits.begin(), bits.end(), false);
}

// Return the total number of bits
std::size_t BitArray::size() const {
    return bits.size();
}
