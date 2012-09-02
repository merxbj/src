#pragma once

#include "Response.h"
#include "RequestProcessor.h"

namespace RobotCommon
{
    class Processable 
    {
    public:
        Processable(void);
        virtual ~Processable(void);

        virtual Response process(RequestProcessor processor) = 0;
    };
}