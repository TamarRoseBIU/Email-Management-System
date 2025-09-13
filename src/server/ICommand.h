#pragma once
#ifndef ICOMMAND_H
#define ICOMMAND_H

#include <string>
#include "CommandTranslator.h"


class ICommand {
private:
    int commandStatus;
public:
    virtual CommandStatus execute(const std::string& args) = 0;
    virtual ~ICommand() noexcept = default;
};

#endif // ICOMMAND_H

    