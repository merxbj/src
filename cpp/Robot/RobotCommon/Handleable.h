#pragma once

class ResponseHandler;

namespace RobotCommon
{
    class Handleable
    {
    public:
        Handleable(void);
        virtual ~Handleable(void);

        virtual void Handle(ResponseHandler handler) = 0;
    };
}

