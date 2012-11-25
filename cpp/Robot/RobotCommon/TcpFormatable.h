#pragma once

#include <string>

using std::string;

namespace RobotCommon
{
    class TcpFormatable
    {
    public:
        TcpFormatable(void);
        virtual ~TcpFormatable(void);

        virtual string formatForTcp() = 0;
        virtual bool parseParamsFromTcp(string params);
    };
}
