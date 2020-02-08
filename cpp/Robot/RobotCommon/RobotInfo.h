#pragma once

#include "Direction.h"
#include "Vector.h"

namespace RobotCommon
{
    class RobotInfo
    {
    public:
        RobotInfo(int x = 0, int y = 0, Direction _direction = Unknown) : position(Position(x, y)), direction(_direction) {}
        virtual ~RobotInfo(void) {}

        Position GetPosition(void) const { return position; }
        Direction GetDiretcion(void) const { return direction; }

    protected:
        Position position;
        Direction direction;
    };
}
