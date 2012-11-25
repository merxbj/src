#pragma once

#include <exception>
#include <string>
#include "StringUtils.h"

using std::exception;
using std::wstring;

namespace RobotCommon
{
    class MissbehavedRequestProcessorException : public exception
    {
    public:
        MissbehavedRequestProcessorException(wstring message) : exception(StringUtils::Format("%S", message.c_str()).c_str()) {}
    };
}
