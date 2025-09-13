cmake_minimum_required(VERSION 3.10)

project(BloomFilterTests)

set(CMAKE_CXX_STANDARD 17)

include_directories(../src)

include(FetchContent)
FetchContent_Declare(
    googletest
   # URL https://github.com/google/googletest/archive/refs/tags/v1.13.0.zip
    URL https://github.com/google/googletest/archive/refs/tags/release-1.11.0.zip
)


set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

file(GLOB SERVER_SRC ../src/server/*.cpp)
file(GLOB CLIENT_SRC ../src/client/*.cpp)

# add_executable(runTests
#     AddNewUrlTest.cpp
#     CheckMatchUrlTest.cpp
#     HashFunctionTest.cpp
#     BitArray_test.cpp
#     FileIOHandler_test.cpp
#     Url_test.cpp
#     GetInputWithAtLeastTwoNumbersTest.cpp
#     BloomFilter_test.cpp
#     AddHashParam_test.cpp
#     HashManager_test.cpp
#     ManageCommandTest.cpp
#     Delete_Url_test.cpp
#     CommandsMenu_test.cpp
#     CommandTranslator_test.cpp
#     test.cpp
#     ${SERVER_SRC}
#     ${CLIENT_SRC}
# )



add_executable(runTests
    AddNewUrlTest.cpp
    CheckMatchUrlTest.cpp
    HashFunctionTest.cpp
    BitArray_test.cpp
    FileIOHandler_test.cpp
    Url_test.cpp
    GetInputWithAtLeastTwoNumbersTest.cpp
    BloomFilter_test.cpp
    AddHashParam_test.cpp
    HashManager_test.cpp
    ManageCommandTest.cpp
    Delete_Url_test.cpp
    CommandsMenu_test.cpp
    CommandTranslator_test.cpp
    Server_And_client_test.cpp
    test.cpp
    ../src/server/BloomFilter.cpp
    ../src/server/AddNewUrl.cpp
    ../src/server/CheckMatchUrl.cpp
    ../src/server/HashFunction.cpp
    ../src/server/BitArray.cpp
    ../src/server/FileIOHandler.cpp
    ../src/server/HashManager.cpp
    ../src/server/Url.cpp
    ../src/server/validInput.cpp
    ../src/server/AddHashParam.cpp
    ../src/server/DeleteUrl.cpp
    ../src/server/CommandTranslator.cpp
    ../src/server/CommandsMenu.cpp
    ../src/server/Manage_Command.cpp
    ../src/server/ServerApp.cpp
    ../src/server/ServerSocket.cpp
)







target_link_libraries(runTests gtest_main)
