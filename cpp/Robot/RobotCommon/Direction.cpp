#include "Direction.h"
#include "Vector.h"
#include <algorithm>

using RobotCommon::Direction;
using RobotCommon::DirectionUtils;
using RobotCommon::Vector;
using std::map;
using std::copy;
using std::back_inserter;
using std::begin;
using std::end;
using std::next;
using std::find;

bool DirectionUtils::initialized = false;
list<Direction> DirectionUtils::directionRotationOrder;
map<Direction, Vector> DirectionUtils::dirToVec;
map<Vector, Direction> DirectionUtils::vecToDir;

void DirectionUtils::ensureInitialized(void)
{
    if (!initialized)
    {
        initialize();
    }
}

void DirectionUtils::initialize(void)
{
    Direction dirs[] = {North, West, South, East};
    copy(begin(dirs), end(dirs), back_inserter(directionRotationOrder));

    dirToVec[North]   = Vector( 0, 1);
    dirToVec[East]    = Vector( 1, 0);
    dirToVec[South]   = Vector( 0,-1);
    dirToVec[West]    = Vector(-1, 0);
    dirToVec[Unknown] = Vector( 0, 0);

    vecToDir[Vector( 0, 1)] = North;
    vecToDir[Vector( 1, 0)] = East;
    vecToDir[Vector( 0,-1)] = South;
    vecToDir[Vector(-1, 0)] = West;
    vecToDir[Vector( 0, 0)] = Unknown;

    initialized = true;
}

Direction DirectionUtils::GetNextDirection(Direction current)
{
    ensureInitialized();

    auto itNext = next(find(begin(directionRotationOrder), end(directionRotationOrder), current));
    return (itNext != end(directionRotationOrder) ? *itNext : *(begin(directionRotationOrder)));
}

Vector DirectionUtils::ToVector(Direction direction)
{
    ensureInitialized();

    return dirToVec[direction];
}

Direction DirectionUtils::FromVector(Vector vector)
{
    ensureInitialized();

    return vecToDir[vector];
}

wstring DirectionUtils::ToString(Direction direction)
{
    switch (direction)
    {
    case North:
        return L"North";
    case South:
        return L"South";
    case East:
        return L"East";
    case West:
        return L"West";
    default:
        return L"Unknown";
    };
}