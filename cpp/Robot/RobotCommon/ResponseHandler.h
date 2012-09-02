#pragma once

#include <string>

using std::wstring;

namespace RobotCommon
{
    class ResponseHandler
    {
    public:
        ResponseHandler(void);
        virtual ~ResponseHandler(void);

        virtual void handleOk(int x, int y) = 0;
        virtual void handleIdentification(wstring address) = 0;
        virtual void handleSuccess(wstring secretMessage) = 0;
        virtual void handleUnknownRequest() = 0;
        virtual void handleCrash() = 0;
        virtual void handleCannotPickUp() = 0;
        virtual void handleProcessorDamaged(int damagedProcessor) = 0;
        virtual void handleProcessorOk() = 0;
        virtual void handleCrumbled() = 0;
    };
}