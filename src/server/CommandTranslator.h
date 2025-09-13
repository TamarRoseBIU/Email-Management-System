#ifndef COMMAND_TRANSLATOR_H
#define COMMAND_TRANSLATOR_H

#include <string>
#include <unordered_map>

// enums 
enum class CommandType {
    POST,
    GET,
    DELETE,
    INVALID
};

enum class CommandStatus {
    URL_CREATED,
    URL_DELETED,
    INVALID_URL,
    CHECK_FALSE,
    CHECK_TRUE_TRUE,
    CHECK_TRUE_FALSE,
    URL_IS_ALREADY_EXISTS,
    URL_FOR_DELETE_IS_NOT_EXISTS,
    INVALID_COMMAND
};

class CommandTranslator {
public:
    static CommandType parseCommandType(const std::string& input);
    static std::string statusToString(CommandStatus status);
};

#endif // COMMAND_TRANSLATOR_H

