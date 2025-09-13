
#include "gtest/gtest.h"
#include "../src/server/BitArray.h"
#include <set>

// Test fixture for BitArray
class BitArrayTest : public ::testing::Test {
protected:
   BitArray* bitArray;

   void SetUp() override {
       bitArray = new BitArray(10); // Initialize with 10 bits
   }

   void TearDown() override {
       delete bitArray;
   }
};

// Test constructor and size
TEST_F(BitArrayTest, ConstructorAndSize) {
   EXPECT_EQ(bitArray->size(), 10);
   for (std::size_t i = 0; i < bitArray->size(); ++i) {
       EXPECT_FALSE(bitArray->getBit(i)); // All bits should be false initially
   }
}

// Test setBit and getBit
TEST_F(BitArrayTest, SetAndGetBit) {
   bitArray->setBit(3, true);
   EXPECT_TRUE(bitArray->getBit(3));
   bitArray->setBit(3, false);
   EXPECT_FALSE(bitArray->getBit(3));
}

// Test setBit out of range
TEST_F(BitArrayTest, SetBitOutOfRange) {
   bitArray->setBit(15, true);
   EXPECT_EQ(bitArray->size(), 10); // Size should remain the same
}

// Test turnsOnBits
TEST_F(BitArrayTest, TurnsOnBits) {
   std::set<std::size_t> indexes = {1, 3, 5};
   bitArray->turnsOnBits(indexes);
   EXPECT_TRUE(bitArray->getBit(1));
   EXPECT_TRUE(bitArray->getBit(3));
   EXPECT_TRUE(bitArray->getBit(5));
   EXPECT_FALSE(bitArray->getBit(0));
   EXPECT_FALSE(bitArray->getBit(2));
}

// Test clearAll
TEST_F(BitArrayTest, ClearAll) {
   bitArray->setBit(2, true);
   bitArray->setBit(4, true);
   bitArray->clearAll();
   for (std::size_t i = 0; i < bitArray->size(); ++i) {
       EXPECT_FALSE(bitArray->getBit(i)); // All bits should be false
   }
}

// Test getBit out of range
TEST_F(BitArrayTest, GetBitOutOfRange) {
   EXPECT_EQ(bitArray->getBit(15), NULL); // Should return NULL for out-of-range
}

// Test turnsOnBits with empty set
TEST_F(BitArrayTest, TurnsOnBitsEmptySet) {
   std::set<std::size_t> indexes;
   bitArray->turnsOnBits(indexes);
   for (std::size_t i = 0; i < bitArray->size(); ++i) {
       EXPECT_FALSE(bitArray->getBit(i)); // No bits should be turned on
   }
}

// Test setBit on boundary
TEST_F(BitArrayTest, SetBitBoundary) {
   bitArray->setBit(9, true); // Last valid index
   EXPECT_TRUE(bitArray->getBit(9));
   bitArray->setBit(9, false);
   EXPECT_FALSE(bitArray->getBit(9));
}

// Test getBit on boundary
TEST_F(BitArrayTest, GetBitBoundary) {
   EXPECT_FALSE(bitArray->getBit(9)); // Last valid index, should be false initially
   bitArray->setBit(9, true);
   EXPECT_TRUE(bitArray->getBit(9));
}

// Test TurnsAllBits
TEST_F(BitArrayTest, TurnsAllBits) {
   bitArray->TurnsAllBits();
   for (std::size_t i = 0; i < bitArray->size(); ++i) {
   EXPECT_TRUE(bitArray->getBit(i)); // All bits should be true
   }
}


// Test areAllBitsOn when all bits are on
TEST_F(BitArrayTest, AreAllBitsOnTrue) {
   bitArray->TurnsAllBits();
   EXPECT_TRUE(bitArray->areAllBitsOn());
}

// Test areAllBitsOn when not all bits are on
TEST_F(BitArrayTest, AreAllBitsOnFalse) {
   bitArray->setBit(0, true);
   bitArray->setBit(1, true);
   EXPECT_FALSE(bitArray->areAllBitsOn());
}

// Test checkBits when all specified bits are on
TEST_F(BitArrayTest, CheckBitsTrue) {
   std::set<std::size_t> indexes = {2, 4, 6};
   bitArray->turnsOnBits(indexes);
   EXPECT_TRUE(bitArray->checkBits(indexes));
}

// Test checkBits when not all specified bits are on
TEST_F(BitArrayTest, CheckBitsFalse) {
   std::set<std::size_t> indexes = {2, 4, 6};
   bitArray->turnsOnBits({2, 4}); // Only turn on some of the bits
   EXPECT_FALSE(bitArray->checkBits(indexes));
}

// Test size method
TEST_F(BitArrayTest, Size) {
   EXPECT_EQ(bitArray->size(), 10); // Initial size
   BitArray anotherBitArray(20);
   EXPECT_EQ(anotherBitArray.size(), 20); // Different size
}
