#include "FileIOHandler.h"
#include <sstream>
#include <iostream>
//#include <filesystem>

//namespace fs = std::filesystem;


// Constructor
FileIOHandler::FileIOHandler(const std::string& path)
    : filePath(path) {
}

// open file stream for reading according to file path
std::ifstream FileIOHandler::openFileStreamForReading() {

     // Check if file exists by trying to open it for reading
    std::ofstream createFile(filePath, std::ios::app);  
    createFile.close();
    std::ifstream file(filePath);
    // failed
   // fs::path absPath = fs::absolute(filePath); 
   // std::cerr << "Loading data from file: " << absPath << std::endl;
    if (!file.is_open()) {
        // print error
        std::cerr << "Failed to open file for reading: " << filePath << std::endl;
        std::exit(EXIT_FAILURE); 
    }
    return file;
}

// open file stream for writing according to file path (if the file doesn't exist, it will create one)
std::ofstream FileIOHandler::openFileStreamForWriting() {
    std::ofstream file(filePath, std::ios::out);
    if (!file.is_open()) {
        std::cerr << "Failed to open or create file for writing: " << filePath << std::endl;
        std::exit(EXIT_FAILURE);
    }
    return file;
}

// Check that it's a negative island number
size_t FileIOHandler::checkFirstLine(std::string firstLine) {
    size_t bitArraySize = 0;  

    try {
        // Try to convert the first line to a number 
        bitArraySize = std::stoi(firstLine);  
    }
    catch (const std::invalid_argument& e) {
        // If the conversion fails because first line is not a valid number
        std::cerr << "Invalid input, not a number: " << firstLine << std::endl;
        std::exit(EXIT_FAILURE);  
    }
    catch (const std::out_of_range& e) {
        // If the number is out of range of size_t 
        std::cerr << "Number out of range: " << firstLine << std::endl;
        std::exit(EXIT_FAILURE);  
    }
    return bitArraySize;
}

// Updating the bit array according to the data from the file
void FileIOHandler::updateBitArrayData(BloomFilter& bloomFilter , std::string secondLine) {
    // Checks that the number of bits received corresponds to the size of the bit array
    size_t bitArraySize = bloomFilter.getBitArray().size();
    if (secondLine.length() != bitArraySize)
    {
        std::cerr << "Error: Bit array length (" << secondLine.length()<< ") does not match expected size (" << bitArraySize << ")." << std::endl;
        std::exit(EXIT_FAILURE);
    }
    // Updates the data in the bit array
    for (int ind = 0; ind < bitArraySize; ind++) {
        // it is already reset to zero
        if (secondLine[ind] == '0') {
            continue;
        }
        // change to true
        else if (secondLine[ind] == '1') {
            bloomFilter.getBitArray().setBit(ind, true);
        }
        // not the expected format
        else
        {
            std::cerr << "Error: Invalid character in bit array: '" << secondLine[ind]<< "' at index: " << ind << std::endl;
                std::exit(EXIT_FAILURE);
        }

    }

}
void FileIOHandler::readUrlsFromFile(std::ifstream& file, BloomFilter& bloomFilter) {
    std::string line;
    // 1 - bit array size, 2 - bit array content, so urls start from line 3
    size_t lineNum = 1;
    // Read each line from the file
    while (std::getline(file, line)) {
        // Checks if line is empty or contains only whitespace - invalid data
        if (line.find_first_not_of(" \t\r\n") == std::string::npos) {
            std::cerr << "Error: Empty or invalid line at line " << lineNum << std::endl;
            std::exit(EXIT_FAILURE);
        }
        // Create url object and add it to the Bloom Filter
        try {
            //option to ensure url format is valid - pass the checks of url format to url constructor
            Url url(line);
            bloomFilter.addUrlManually(url);
        }
        catch (const std::exception& e) {
            std::cerr << "Error: This is not a valid url " << lineNum
                << ": " << e.what() << std::endl;
            std::exit(EXIT_FAILURE);
        }
        ++lineNum;
    }

}

// Load data from file and organize it in the appropriate classes
BloomFilter FileIOHandler::loadData(std::size_t bitArraySize) {
    std::ifstream fileStream = openFileStreamForReading();
    BloomFilter bloomFilter(bitArraySize);
    readUrlsFromFile(fileStream, bloomFilter);
    // Close the file stream after reading
    fileStream.close();
    // Return the bloom filter object that was created 
    return bloomFilter;
}

// Save the data from the classes to the file according to the appropriate format
void FileIOHandler::saveData(const BloomFilter& bloomFilterObj){
    std::ofstream fileStream = openFileStreamForWriting();
    // Write the urls
    for (const auto& url : bloomFilterObj.getInsertedUrls()) {
        fileStream << url.getUrl() << std::endl;
    }
    // Close the file stream after writing
    fileStream.close();
}







