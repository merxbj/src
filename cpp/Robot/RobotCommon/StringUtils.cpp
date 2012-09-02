#include "StringUtils.h"
#include "stdarg.h"

using RobotCommon::StringUtils;

wstring StringUtils::Format(std::wstring fmt, ...) {

    int size = 100;
    wstring str;
    va_list ap;

    bool done = false;
    while (!done) 
    {
        str.resize(size);
        va_start(ap, fmt);
        int n = _vsnwprintf_s(const_cast<wchar_t*>(str.c_str()), size, _TRUNCATE, fmt.c_str(), ap);
        va_end(ap);
        if (n > -1 && n < size)
        {
            str.resize(n);
            done = true;
        }
        if (!done)
        {
            if (n > -1)
            {
                size=n+1;
            }
            else
            {
                size*=2;
            }
        }
    }

    return str;
}

string StringUtils::Format(std::string fmt, ...) {

    int size = 100;
    string str;
    va_list ap;

    bool done = false;
    while (!done) 
    {
        str.resize(size);
        va_start(ap, fmt);
        int n = vsnprintf_s(const_cast<char*>(str.c_str()), size, _TRUNCATE, fmt.c_str(), ap);
        va_end(ap);
        if (n > -1 && n < size)
        {
            str.resize(n);
            done = true;
        }
        if (!done)
        {
            if (n > -1)
            {
                size=n+1;
            }
            else
            {
                size*=2;
            }
        }
    }

    return str;
}