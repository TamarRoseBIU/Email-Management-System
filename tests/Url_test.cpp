// This file contains unit tests for the BloomFilter class, focusing on URL-related functionality.

#include <gtest/gtest.h>
#include "../src/server/Url.h"

// Empty constructor + getUrl
TEST(UrlTest, Empty_ctor) {
   Url url;
   EXPECT_EQ(url.getUrl(), "");
}

// Constructor with url + getUrl
TEST(UrlTest, ValidUrl) {
   Url url("http://example.com");
   EXPECT_EQ(url.getUrl(), "http://example.com");
}

// Copy constructor + getUrl
TEST(UrlTest, CopyConstructor) {
   Url url1("http://example.com");
   Url url2(url1);
   EXPECT_EQ(url2.getUrl(), "http://example.com");
}

// Set url + getUrl
TEST(UrlTest, SetUrl) {
   Url url;
   url.setUrl("http://example.com");
   EXPECT_EQ(url.getUrl(), "http://example.com");
}

// Comparison operator + setUrl
TEST(UrlTest, ComparisonOp) {
   Url url1;
   url1.setUrl("http://example.com");
   Url url2;
   url2.setUrl("http://example.com");
   EXPECT_TRUE(url1 == url2);

   Url url3;
   url3.setUrl("http://examploid.com");
   EXPECT_FALSE(url1 == url3);
}
