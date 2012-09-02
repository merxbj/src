#pragma once

#include <string>
#include "Response.h"

using std::wstring;

namespace RobotCommon
{
    class RequestProcessor
    {
    public:
        RequestProcessor(void);
        virtual ~RequestProcessor(void);

        virtual Response processStep() = 0;
        virtual Response processTurnLeft() = 0;
        virtual Response processPickUp() = 0;
        virtual Response processProcessorRepair(int processorToRepair) = 0;
        virtual Response processUnknown() = 0;
        virtual wstring getExpectedAddress() = 0;
    }
}