#pragma once

#include <string>

using std::wstring;

namespace RobotCommon
{
    class TcpFormatable
    {
    public:
        TcpFormatable(void);
        virtual ~TcpFormatable(void);

        virtual wstring formatForTcp() = 0;
        virtual bool parseParamsFromTcp(wstring params);
    };
}