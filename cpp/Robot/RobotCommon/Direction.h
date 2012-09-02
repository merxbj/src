#pragma once

#include "Vector.h"
#include <list>
#include <map>
#include <string>

using std::map;
using std::list;
using std::string;

namespace RobotCommon
{
    enum Direction
    {
        North, West, South, East, Unknown
    };

    class DirectionUtils
    {
    private:
        DirectionUtils(void);
        static bool initialized;
        static void initialize(void);
        static void ensureInitialized(void);
        static list<Direction> directionRotationOrder;
        static map<Direction, Vector> dirToVec;
        static map<Vector, Direction> vecToDir;

    public:
        static Direction GetNextDirection(Direction current);
        static Vector ToVector(Direction direction);
        static Direction FromVector(Vector vector);
        static wstring ToString(Direction direction);
    };
}