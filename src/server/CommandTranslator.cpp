#include "CommandTranslator.h"

CommandType CommandTranslator::parseCommandType(const std::string& input) {
    if (input == "POST") return CommandType::POST;
    if (input == "GET") return CommandType::GET;
    if (input == "DELETE") return CommandType::DELETE;
    return CommandType::INVALID;
}

std::string CommandTranslator::statusToString(CommandStatus status) {
    static const std::unordered_map<CommandStatus, std::string> statusMap = {
        {CommandStatus::URL_CREATED, "201 Created"},
        {CommandStatus::URL_DELETED, "204 No Content"},
        {CommandStatus::INVALID_URL, "400 Bad Request"}, // CHECK THIS
        {CommandStatus::CHECK_FALSE, "200 Ok\n\n\nfalse"},
        {CommandStatus::CHECK_TRUE_TRUE, "200 Ok\n\n\ntrue true"},
        {CommandStatus::CHECK_TRUE_FALSE, "200 Ok\n\n\ntrue false"},
        {CommandStatus::URL_IS_ALREADY_EXISTS, "201 Created"}, 
        {CommandStatus::URL_FOR_DELETE_IS_NOT_EXISTS, "404 Not Found"},
        {CommandStatus::INVALID_COMMAND, "400 Bad Request"}
    };

    auto it = statusMap.find(status);
    return (it != statusMap.end()) ? it->second : "400 Bad Request";
}
