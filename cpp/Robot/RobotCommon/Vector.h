#pragma once

#include <string>
#include "StringUtils.h"

using std::wstring;

namespace RobotCommon
{
    struct Vector
    {
        Vector(int _x = 0, int _y = 0) : x(_x), y(_y) {}
        ~Vector(void) {}

        wstring ToString() const { return StringUtils::Format(L"(%d,%d)", x, y); }

        int x;
        int y;
    };

    inline bool operator<(const Vector& lhs, const Vector& rhs) { return (lhs.x < rhs.x) ? true : (lhs.x > rhs.x) ? false : (lhs.y < rhs.y); }
    inline bool operator==(const Vector& lhs, const Vector& rhs) { return ((lhs.x == rhs.x) && (lhs.y == rhs.y)); }
    inline Vector operator-(const Vector& lhs, const Vector& rhs) { return Vector(lhs.x - rhs.x, lhs.y - rhs.y); }

    typedef Vector Position;
}
