#pragma once
#ifndef URL_H
#define URL_H

#include <string>

class Url {
private:
    std::string urlContent;
    // add a field of false positive?

public:
    // Default constructor
    Url();

    // Constructor to initialize the URL value
    Url(const std::string& url);

    // Copy constructor to create a copy of another
    Url(const Url& otherUrl);

    // Destructor
    ~Url();

    // Getter function to access the URL value
    std::string getUrl() const;

    // Setter function to update the URL value
    void setUrl(const std::string& value);

    // Comparison operator to compare URL objects
    bool operator==(const Url& otherUrl) const;

    bool operator<(const Url& other) const;
};

#endif // URL_H

