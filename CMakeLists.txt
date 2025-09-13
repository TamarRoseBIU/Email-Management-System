cmake_minimum_required(VERSION 3.10)
project(BloomFilterProject)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE Debug)

file(GLOB SERVER_SRC src/server/*.cpp)
file(GLOB CLIENT_SRC src/client/*.cpp)

add_executable(BloomFilterApp
 #   src/main.cpp
    src/App.cpp
  #  src/BloomFilter.cpp
 #   src/AddNewUrl.cpp
  #  src/CheckMatchUrl.cpp
  #  src/HashFunction.cpp
  #  src/BitArray.cpp
  #  src/FileIOHandler.cpp
  #  src/HashManager.cpp
  #  src/Url.cpp
  #  src/validInput.cpp
  #  src/AddHashParam.cpp
  #  src/DeleteUrl.cpp
  #  src/CommandTranslator.cpp
    ${SERVER_SRC}
    ${CLIENT_SRC}
)


set_target_properties(BloomFilterApp PROPERTIES OUTPUT_NAME "BloomFilterApp")


add_custom_command(TARGET BloomFilterApp POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    ${CMAKE_SOURCE_DIR}/data $<TARGET_FILE_DIR:BloomFilterApp>/data
)
include_directories(${CMAKE_SOURCE_DIR}/src/server)
