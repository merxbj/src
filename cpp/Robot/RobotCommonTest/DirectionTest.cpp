#include "stdafx.h"
#include "CppUnitTest.h"
#include "RobotCommonTestCommon.h"
#include "..\RobotCommon\Direction.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;
using RobotCommon::Direction;
using RobotCommon::DirectionUtils;
using RobotCommon::Vector;

namespace RobotCommonTest
{
	TEST_CLASS(DirectionTest)
	{
	public:
		
		TEST_METHOD(RotationTest)
		{
            Direction dir = Direction::North;
            Assert::AreEqual(Direction::West, dir = DirectionUtils::GetNextDirection(dir));
            Assert::AreEqual(Direction::South, dir = DirectionUtils::GetNextDirection(dir));
            Assert::AreEqual(Direction::East, dir = DirectionUtils::GetNextDirection(dir));
            Assert::AreEqual(Direction::North, dir = DirectionUtils::GetNextDirection(dir));
		}

        TEST_METHOD(VectorToDirectionTest)
        {
            Assert::AreEqual(Direction::South, DirectionUtils::FromVector(Vector( 0,-1)));
        }

        TEST_METHOD(DirectionToVectorTest)
        {
            Assert::AreEqual(Vector( 1, 0), DirectionUtils::ToVector(Direction::East));
        }
	};
}
