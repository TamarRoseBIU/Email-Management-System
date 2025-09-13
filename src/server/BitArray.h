#ifndef BITARRAY_H
#define BITARRAY_H

#include <vector>
#include <cstddef>
#include <set>

class BitArray {
private:
    std::vector<bool> bits;

public:
    // Constructor to initialize the bit array with a given size
    BitArray(std::size_t size);

    // Set the bit at a specific index to a given bool value
    void setBit(std::size_t index, bool value);

    // Turns on the bits of the given indexes
    void turnsOnBits(std::set<std::size_t> indexes);

    // Turns on all bits
    void TurnsAllBits();

    // Get the value of the bit at a specific index
    bool getBit(std::size_t index) const;

    // Checks if all the bits received are on 
    bool checkBits(std::set<std::size_t> indexes) const;

    // Checks if all bits are on
    bool areAllBitsOn() const;

    // Clear all bits - reset the array
    void clearAll();

    // Return the size of the bit array
    std::size_t size() const;
    
    void printBits();
};

#endif // BITARRAY_H

