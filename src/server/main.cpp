#include "ServerApp.h" // Make sure this file defines the App class
#include "BloomFilter.h" // Make sure this file defines the BloomFilter class


int main(int argc, char* argv[]) {

    if (argc < 4) 
    {
        // check num of arguments
        exit(1);
    }

    int port;
    std::size_t bitArraySize;
    std::vector<std::size_t> hashFuncRounds;
    try
    {
        // Convert port from string (argv[0] is the file)
        port = std::stoi(argv[1]);

        // Convert bit array size from 
        if (argv[2][0] == '-') {
            exit(1);
        }
        bitArraySize = std::stoul(argv[2]);
        if (bitArraySize == 0) {
            exit(1);
        }

        // Convert hash function rounds to vector
        for (int i = 3; i < argc; ++i) {
            if (argv[i][0] == '-') {
                exit(1);
            }
            std::size_t round = std::stoul(argv[i]);
            if (round == 0) {
                exit(1);
            }
            hashFuncRounds.push_back(round);
        }
    }
    catch(const std::exception& e)
    {
        exit(1);
    }
    // check port is valid 
    if(1024 <= port && port <= 65535){
        // valid port, continue
    }
    else
    {
        exit(1);
    }   
    // Create an instance of the App Server class
    ServerApp serverApp;

    // Run the main application in server side
    serverApp.toDo(port, bitArraySize, hashFuncRounds);

    return 0;
}
