#pragma once

#include <string>
#include <list>
#include <memory>
#include "Response.h"
#include "Processable.h"

using std::wstring;
using std::string;
using std::list;
using std::shared_ptr;

namespace RobotCommon
{
    enum RequestType
    {
        Repair, Step, TurnLeft, PickUp, RequestUnknown
    };

    class RequestProcessor;

    class Request : public TcpFormatable, public Processable
    {
    public:
        Request(string _address = "/nobody/") : address(_address) {}
        virtual ~Request(void) {}

        virtual RequestType GetRequestType(void) const = 0;
        virtual wstring GetName() const = 0;

        shared_ptr<Response> Process(RequestProcessor* processor);
        string getAdress() { return address; }

    protected: 
        virtual shared_ptr<Response> route(RequestProcessor* processor) const = 0;
        virtual list<Response*> getSupportedResponses() const = 0;
        virtual bool parseParamsFromTcp(string params) = 0;

        bool isResponseSupported(Response* response) const;
        void setAdress(string _adress) { address = _adress; }

    private:
        string address;
    };

    class RequestProcessor
    {
    public:
        RequestProcessor(void);
        virtual ~RequestProcessor(void);

        virtual shared_ptr<Response> processStep() = 0;
        virtual shared_ptr<Response> processTurnLeft() = 0;
        virtual shared_ptr<Response> processPickUp() = 0;
        virtual shared_ptr<Response> processProcessorRepair(int processorToRepair) = 0;
        virtual shared_ptr<Response> processUnknown() = 0;
        virtual wstring getExpectedAddress() = 0;
    };
}
