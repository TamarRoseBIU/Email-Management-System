#include "DeleteUrl.h"
#include "Url.h"
// Ctor to initialize bloom filter instance
DeleteUrl::DeleteUrl(BloomFilter& bloomFilterRef):
    bloomFilter(bloomFilterRef) {}

// executing the delete command
CommandStatus DeleteUrl::execute(const std::string& input){
    Url url_to_remove(input);
    if (!bloomFilter.isUrlValid(url_to_remove)) {
        return CommandStatus::INVALID_URL; // URL is invalid
    }
    if (bloomFilter.deleteUrl(url_to_remove)){
        return CommandStatus::URL_DELETED; // URL deleted successfully
    } else {
        return CommandStatus::URL_FOR_DELETE_IS_NOT_EXISTS; // URL not found in the filter
    }
}