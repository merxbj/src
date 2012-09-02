#include "stdafx.h"
#include "CppUnitTest.h"
#include "..\RobotCommon\StringUtils.h"

using namespace Microsoft::VisualStudio::CppUnitTestFramework;
using RobotCommon::StringUtils;

namespace RobotCommonTest
{
	TEST_CLASS(StringUtilsTest)
	{
	public:
		
		TEST_METHOD(BasicTest)
		{
            wstring result = StringUtils::Format(L"Let x=%d and y=%d then x+y=%d", 1, 1, 2);
            Assert::AreEqual(wstring(L"Let x=1 and y=1 then x+y=2"), result);
		}

	};
}