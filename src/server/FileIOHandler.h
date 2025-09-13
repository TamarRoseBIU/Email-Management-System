#ifndef FILEIOHANDLER_H
#define FILEIOHANDLER_H

#include "IIOHandler.h"
#include <fstream>
#include <cstring>  
#include <string>

class FileIOHandler : public IIOHandler {

/*
* the format of the file:
* < url 1 >
* .....
* < url n >
*/

private:

    std::string filePath;
    // open file stream for reading according to file path
    std::ifstream openFileStreamForReading();

    // open file stream for writing according to file path
    std::ofstream openFileStreamForWriting();

    // Check that it's a negative island number
    size_t checkFirstLine(std::string firstLine);

    // Updating the bit array according to the data from the file
    void updateBitArrayData(BloomFilter& bloomFilter, std::string secondLine);

    // Reads urls from a file and add them to the Bloom Filter
    void readUrlsFromFile(std::ifstream& file, BloomFilter& bloomFilter);

public:

    // Constructor - Compiler conversions from any string to this object will not be allowed (because it - explicit)
    explicit FileIOHandler(const std::string& path);

    // Load data from file and organize it in the appropriate classes
    BloomFilter loadData(std::size_t bitArraySize) override;

    // Save the data from the classes to the file according to the appropriate format
     void saveData(const  BloomFilter& bloomFilterObj) override;
};

#endif // FILEIOHANDLER_H

