cmake_minimum_required(VERSION 3.10)
project(BloomFilterApp)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE Debug)


# Add all source files here
add_executable(BloomFilterApp
    server/main.cpp
    App.cpp
    server/BloomFilter.cpp
    server/AddNewUrl.cpp
    server/CheckMatchUrl.cpp
    server/HashFunction.cpp
    server/BitArray.cpp
    server/FileIOHandler.cpp
    server/HashManager.cpp
    server/Url.cpp
    server/validInput.cpp
    server/AddHashParam.cpp
)

set_target_properties(BloomFilterApp PROPERTIES OUTPUT_NAME "BloomFilterApp")
# If you need any additional libraries, link them here
# target_link_libraries(BloomFilterApp ...)


# Copy data folder into the binary directory (where executable is)
#add_custom_command(TARGET BloomFilterApp POST_BUILD
#    COMMAND ${CMAKE_COMMAND} -E copy_directory
#    ${CMAKE_SOURCE_DIR}/data $<TARGET_FILE_DIR:BloomFilterApp>/data
#)

add_custom_command(TARGET BloomFilterApp POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    ${CMAKE_SOURCE_DIR}/../data $<TARGET_FILE_DIR:BloomFilterApp>/data
)
