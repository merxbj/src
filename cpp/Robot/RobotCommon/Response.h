#pragma once

#include "Handleable.h"
#include "TcpFormatable.h"
#include <string>

using std::wstring;
using std::string;

namespace RobotCommon
{
    enum ResponseType
    {
        Crash, Ok, Success, Crumbled, Identification, CannotPickUp, ProcessorOk, ProcessorDamaged, UnknownRequest, ResponseUnknown
    };

    class Response : public TcpFormatable, public Handleable
    {
    public:
        virtual bool isEndGame() = 0;
        virtual wstring GetName() const = 0;
        virtual bool parseParamsFromTcp(string params) = 0;
        virtual ResponseType GetResponseType(void) const = 0;
    };

    inline bool operator==(const Response& lhs, const Response& rhs) { return (lhs.GetResponseType() == rhs.GetResponseType()); }

    class ResponseHandler
    {
    public:
        ResponseHandler(void);
        virtual ~ResponseHandler(void);

        virtual void handleOk(int x, int y) = 0;
        virtual void handleIdentification(string address) = 0;
        virtual void handleSuccess(string secretMessage) = 0;
        virtual void handleUnknownRequest() = 0;
        virtual void handleCrash() = 0;
        virtual void handleCannotPickUp() = 0;
        virtual void handleProcessorDamaged(int damagedProcessor) = 0;
        virtual void handleProcessorOk() = 0;
        virtual void handleCrumbled() = 0;
    };
}
