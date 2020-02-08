#include "Request.h"
#include "Exception.h"
#include <algorithm>
#include "StringUtils.h"

using namespace RobotCommon;
using std::shared_ptr;
using std::find;

shared_ptr<Response> Request::Process(RequestProcessor* processor)
{
    shared_ptr<Response> response = route(processor);
    if (!isResponseSupported(response.get())) 
    {
        
        throw new MissbehavedRequestProcessorException(
            StringUtils::Format(L"Unsupported response %s on request %s!",
            response->GetName(),
            GetName()));
    }

    return response;
}

bool Request::isResponseSupported(Response* response) const
{
    list<Response*> supportedResponses = getSupportedResponses();
    auto supported = find(begin(supportedResponses), end(supportedResponses), response);
    return supported != end(supportedResponses);
}