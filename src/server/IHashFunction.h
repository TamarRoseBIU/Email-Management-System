#ifndef HASHFUNCTION_H
#define HASHFUNCTION_H

#include "Url.h"

class IHashFunction {
public:
    virtual ~IHashFunction() = default;

    // Virtual function to hash a url - return array of bits
    virtual std::size_t hash(const Url& url) const = 0;

};

#endif // HASHFUNCTION_H
