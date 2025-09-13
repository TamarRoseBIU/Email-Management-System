
#include "gtest/gtest.h"
#include "../src/server/BloomFilter.h"
#include "../src/server/BitArray.h"
#include "../src/server/CheckMatchUrl.h"
#include "../src/server/IHashFunction.h"
#include "../src/server/HashFunction.h"
int main(int argc, char **argv){
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();

}
