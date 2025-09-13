#include "Url.h"

// Default constructor 
Url::Url() : urlContent("") {}

// Constructor to initialize the URL value
Url::Url(const std::string& url) : urlContent(url) {}

// Copy constructor to create a copy of another
Url::Url(const Url& otherUrl) : urlContent(otherUrl.getUrl()) {}

// Destructor 
Url::~Url() {}

// Getter function to access the URL value
std::string Url::getUrl() const {
    return urlContent;
}

// Setter function to update the URL value
void Url::setUrl(const std::string& value) {
    urlContent = value;
}

// Comparison operator to compare URL objects
bool Url::operator==(const Url& otherUrl) const {
    return urlContent == otherUrl.getUrl();
}

bool Url::operator<(const Url& other) const {
    return urlContent < other.urlContent;
}
