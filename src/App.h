#pragma once

#include <set>
#include <iostream>
#include "ICommand.h"
#include "BloomFilter.h"  // Include the BloomFilter class
#include "validInput.h"  // Include the header file for the input validation function 
#include "AddNewUrl.h"  // Include the header file for the AddNewUrl command
#include "CheckMatchUrl.h"  // Include the header file for the CheckMatchUrl command
#include "CommandTranslator.h"  // Include the header file for the CommandTranslator class
#include "DeleteUrl.h"  // Include the header file for the DeleteUrl command

class Application {

public:
    Application();
    void toDo();
};

