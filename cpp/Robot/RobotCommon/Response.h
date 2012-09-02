#pragma once

#include "Handleable.h"
#include "TcpFormatable.h"
#include <string>

using std::wstring;

namespace RobotCommon
{
    class Response : public TcpFormatable, public Handleable
    {
    public:
        Response(void);
        virtual ~Response(void);

        virtual bool isEndGame() = 0;

        virtual bool parseParamsFromTcp(wstring params) {
            return true;
        }
    };
}