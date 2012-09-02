#pragma once

#include <memory>
#include "Response.h"
#include "Request.h"

using std::shared_ptr;

namespace RobotCommon
{
    class RequestProcessor;

    class Processable
    {
    public:
        Processable(void);
        virtual ~Processable(void);

        virtual shared_ptr<Response> process(RequestProcessor* processor) = 0;
    };
}
