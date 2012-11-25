#pragma once

#include <string>

using std::wstring;
using std::string;

namespace RobotCommon
{
    class StringUtils
    {
    private:
        StringUtils(void) {};

    public:
        static wstring Format(wstring fmt, ...);
        static string Format(string fmt, ...);
    };
}
