#pragma once

#include <string>

using std::wstring;

namespace RobotCommon
{
    class StringUtils
    {
    private:
        StringUtils(void) {};

    public:
        static wstring Format(std::wstring fmt, ...);
    };
}
