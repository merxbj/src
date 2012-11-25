#include "stdafx.h"
#include "CppUnitTest.h"
#include "RobotCommonTestCommon.h"
#include "..\RobotCommon\Vector.h"
#include <map>

using namespace Microsoft::VisualStudio::CppUnitTestFramework;
using RobotCommon::Vector;
using std::map;

namespace RobotCommonTest
{		
    TEST_CLASS(VectorTest)
    {
    public:
        
        TEST_METHOD(SubstractionTest)
        {
            Vector v = Vector(1,2);
            Vector zero = v - Vector(1,2);
            Assert::AreEqual(0, zero.x);
            Assert::AreEqual(0, zero.y);
        }

        TEST_METHOD(ComparisonTest)
        {
            Vector v1 = Vector(1,2);
            Vector v2 = Vector(1,2);
            Assert::AreEqual(v1, v2);
        }

        TEST_METHOD(MapTest)
        {
            auto vectors = map<int, Vector>();
            vectors[1] = Vector(1,1);
            vectors[2] = Vector(2,2);
            vectors[3] = Vector(3,3);

            Assert::AreEqual(Vector(1,1), vectors[1]);
            Assert::AreEqual(Vector(2,2), vectors[2]);
            Assert::AreEqual(Vector(3,3), vectors[3]);
        }

    };
}
