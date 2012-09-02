#include "Vector.h"
#include "StringUtils.h"
#include "math.h"

using RobotCommon::Vector;

Vector::Vector(int _x /* = 0 */, int _y /* = 0 */)  : 
    x(_x), 
    y(_y)
{
}

Vector::~Vector(void)
{
}

wstring Vector::ToString() const
{
    return StringUtils::Format(L"(%d,%d)", x, y);
}
