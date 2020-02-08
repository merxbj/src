#pragma once

#include "stdafx.h"
#include "CppUnitTest.h"
#include "..\RobotCommon\Direction.h"
#include "..\RobotCommon\Vector.h"
#include <string>

using std::wstring;
using RobotCommon::Vector;
using RobotCommon::Direction;
using RobotCommon::DirectionUtils;

namespace Microsoft
{ 
    namespace VisualStudio
    { 
        namespace CppUnitTestFramework
        {
            template<> static wstring ToString<Vector>(const Vector& v) 
            {
                return v.ToString();
            }
        }
    }
}

namespace Microsoft
{ 
    namespace VisualStudio
    { 
        namespace CppUnitTestFramework
        {
            template<> static wstring ToString<Direction>(const Direction& d) 
            {
                return DirectionUtils::ToString(d);
            }
        }
    }
}
