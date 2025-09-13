#pragma once
#ifndef FROMURLTOINDEXES_H
#define FROMURLTOINDEXES_H

#include "Url.h"
#include "BloomFilter.h"
#include <regex>
#include <string>
#include <fstream>
#include <set>
#include <iostream>

// Get a url, hash it, and return the indexes to change
std::set<std::size_t> from_url_to_indexes(BloomFilter& bloom_filter, const Url& url);

#endif