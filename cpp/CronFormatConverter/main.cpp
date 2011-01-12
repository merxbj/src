#include "windows.h"
#include <iostream>
#include "CRONExpression.h"
#include "CommandLine.h"

bool resetTimer(HANDLE timer, int hours, int minutes);
void printUsage();

int main(int argc, char* argv[])
{
	try
	{
		CCommandLinePtr cl = CCommandLine::parse(argc, argv);
		CCronSchedulePattern expression = CCronSchedulePattern(cl->getCronExpression());
		ScheduleTimesType schedule = ScheduleTimesType();
		expression.buildTodaySchedule(&schedule);

		std::cout << "Configured schedule expands to following occurences during this day:\n";
		for (ScheduleTimesType::const_iterator it = schedule.begin(); it != schedule.end(); it++)
		{
			int minutes = (*it) % 100;
			int hours = (*it) / 100;
			printf("%02d:%02d\n", hours, minutes);
		}

		std::cout << "Hit the <Control - C> to exit." << std::endl;
		HANDLE timer = CreateWaitableTimer(NULL, false, L"CronSimulatorTimer");
		for (ScheduleTimesType::const_iterator it = schedule.begin(); it != schedule.end(); it++)
		{
			int minutes = (*it) % 100;
			int hours = (*it) / 100;
			if (resetTimer(timer, hours, minutes))
			{
				DWORD waitObject = WaitForSingleObject(timer, INFINITE);
				if (waitObject == WAIT_OBJECT_0)
				{
					std::cout << cl->getDisplayMessage().c_str() << std::endl;
				}
				else
				{
					std::cerr << "WaitForSingleObject awaited for someone who we didn't expect!\n";
				}
			}
		}
	}
	catch (const CArgumentException& ex)
	{
		std::cerr << ex.getMessage().c_str();
		printUsage();
		return -1;
	}
	catch (const CCronInternalException& ex)
	{
		std::cerr << ex.m_strMessage.c_str();
		return -2;
	}
	catch (const CCronException& ex)
	{
		std::cerr << ex.m_strMessage.c_str();
		return -3;
	}

	return 0;
}

bool resetTimer(HANDLE timer, int hours, int minutes)
{
	SYSTEMTIME now;
	GetLocalTime(&now);

	if (now.wHour > hours || now.wMinute >= minutes)
	{
		printf("%02d:%02d already passed or it is now (%02d:%02d). Skipping.\n", hours, minutes, now.wHour, now.wMinute);
		return false;
	}

	__int64 hundretsOfNanosecondsToGo = ((hours - now.wHour) * 60 * 60 * 10000000) + 
		((minutes - now.wMinute) * 60 * 10000000) - 
		(now.wSecond * 10000000); // just a seconds precision
	hundretsOfNanosecondsToGo *= -1; // negative number means relative time

	LARGE_INTEGER dueTime;
	dueTime.LowPart = static_cast<DWORD> (hundretsOfNanosecondsToGo & 0xFFFFFFFF);
	dueTime.HighPart = static_cast<LONG> (hundretsOfNanosecondsToGo >> 32);

	if (!SetWaitableTimer(timer, &dueTime, 0, NULL, NULL, false))
	{
		std::cerr << "Error occured while calling SetWaitableTimer() : " << GetLastError() << std::endl;
	}

	return true;
}

void printUsage()
{
	std::cout << "\nUSAGE:" << std::endl;
	std::cout << "\tCronSimulator [--message message] cron_expression" << std::endl;
	std::cout << "\nDESCRIPTION:" << std::endl;
	std::cout << "\tcron_expression\tCRON Expression (please consult documentation)" << std::endl;
	std::cout << "\tmessage\t\tMessage to be shown when the timer hits." << std::endl;
	std::cout << "\t       \t\tDefault value: \"Hello Scheduled World\"." << std::endl;
	std::cout << "\n\tOPTIONS:" << std::endl;
	std::cout << "\t--message\tConfigre the message to be shown when the timer hits." << std::endl;
}
