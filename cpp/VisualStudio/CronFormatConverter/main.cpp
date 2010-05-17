#include "windows.h"
#include <iostream>
#include <list>
using namespace std;

#define countof(a) (sizeof(a) / sizeof(a[0]))
typedef std::list<std::string> StringList;

typedef struct element_t
{
    int modulo;
    int* list;
    bool all;
	bool omit;

    element_t() : modulo(0), list(NULL), all(true), omit(false) {}
    element_t(int _modulo, int* _list, bool _all, bool _omit) : modulo(_modulo), list(_list), all(_all), omit(_omit) {}
	element_t(int _modulo, int _element, bool _all, bool _omit) : modulo(_modulo), all(_all), omit(_omit) { list = new int[1]; list[0] = _element; }
	~element_t() {delete[] list;}
} element;

const char* months = "janfebmaraprmayjunjulaugsepoctnovdec";
const char* days = "sunmontuewedthufrisat";

enum
{
    CRON_MINUTE,
	CRON_HOUR,
    CRON_DAY_OF_MONTH,
    CRON_MONTH,
    CRON_DAY_OF_WEEK
};

int buildTodaySchedule(const char* cron, int** schedule);
bool isScheduledDay(const element* const* cronElements);
int buildSchedule(const element* const* cronElements, int** schedule);
bool parseCron(const char* cron, element** cronElements);
element* parseSingleCronElement(const char* rawCronElement);
int* parseList(const char* rawList);
int split(const std::string str, const std::string delims, StringList* out);
bool validateCron(const element* const* cronElements);
bool validateList(const int* list, int min, int max);
bool isScheduledHour(int hour, const element* const* cronElements);
bool isScheduledMinute(int minute, const element* const* cronElements);
bool isScheduledValue(int value, const element* cronElement);
bool isScheduledDayOfMonth(int dayOfMonth, const element* const* cronElements);
bool isScheduledMonth(int month, const element* const* cronElements);
bool isScheduledDayOfWeek(int dayOfWeek, const element* const* cronElements);
bool isValueInList(int value, const int* list);

int main()
{
    const char cron[] = "30 */2 * * ?";
    int* schedule = NULL;

    int count = buildTodaySchedule(cron, &schedule);

    for (int i = 0; i < count; i++)
        printf("%02d:%02d\n", schedule[i] / 100, schedule[i] % 100);

    system("PAUSE");
	delete [] schedule;
    return 0;
}

int buildTodaySchedule(const char* cron, int** schedule)
{
    element* cronElements[5];
    if (parseCron(cron, cronElements))
    {
        if (isScheduledDay(cronElements))
            return buildSchedule(cronElements, schedule);
    }

    return 0;
}

bool isScheduledDay(const element* const* const cronElements)
{
    SYSTEMTIME stCurrTime;
    GetLocalTime(&stCurrTime);

	if (!isScheduledMonth(stCurrTime.wMonth, cronElements))
		return false;

	return (cronElements[CRON_DAY_OF_MONTH]->omit) ? 
		isScheduledDayOfWeek(stCurrTime.wDayOfWeek, cronElements) : isScheduledDayOfMonth(stCurrTime.wDay, cronElements);
}

int buildSchedule(const element* const* cronElements, int** schedule)
{
    int scheduleBuf[60*24];
    int i = 0;
    for (int hour = 0; hour < 24; hour++)
    {
        if (isScheduledHour(hour, cronElements))
        {
            for (int minute = 0; minute < 60; minute++)
            {
                if (isScheduledMinute(minute, cronElements))
                {
                    scheduleBuf[i++] = (hour * 100) + minute;
                }
            }
        }
    }
    *schedule = new int[i];
    for (int j = 0; j < i; j++)
        (*schedule)[j] = scheduleBuf[j];

    return i;
}

bool parseCron(const char* cron, element** cronElements)
{
    const char delimiter[] = " ";
    char* cronCopy = new char[strlen(cron) + 1];
    strncpy(cronCopy, cron, strlen(cron));
    cronCopy[strlen(cron)] = '\0';
    int i = 0;
    
    char* token = strtok(cronCopy, delimiter);
    while (token != NULL)
    {
        element* parsedElement = parseSingleCronElement(token);
        if (parsedElement != NULL)
        {
            cronElements[i++] = parsedElement;
        }
        else
        {
            for (i = i - 1; i >= 0; i--)
                delete cronElements[i];
            return false;
        }
        
        token = strtok(NULL, delimiter);
    }

    return ((i == 5) && validateCron(cronElements));
}

element* parseSingleCronElement(const char* rawCronElement)
{
    const char* day = NULL; 
    const char* month = NULL;
    
    if (strcmp(rawCronElement, "*") == 0)
    {
        return new element();
    }
	else if (strcmp(rawCronElement, "?") == 0)
	{
		return new element(0, 0, false, true);
	}
    else if (strstr(rawCronElement, "*/") != NULL)
    {
        int modulo = atoi(rawCronElement + 2);
        if (modulo > 0)
            return new element(modulo, 0, false, false);
    }
    else if ((month = strstr(months, rawCronElement)) != NULL)
    {
        int pos = month - months;
        if (pos % 3 == 0)
            return new element(0, (pos / 3) + 1, false, false);
    }
    else if ((day = strstr(days, rawCronElement)) != NULL)
    {
        int pos = day - days;
        if (pos % 3 == 0)
            return new element(0, pos / 3, false, false);
    }
    else
    {
        int* list = parseList(rawCronElement);
		if (list != NULL)
			return new element (0, list, false, false);
    }

    return NULL;
}

int* parseList(const char* rawList)
{
	int tmp[255];
	int count = 0;
	StringList elms;
	int elmsCount = split(rawList, ",", &elms);
	for (StringList::iterator it = elms.begin(); it != elms.end(); it++)
	{
		StringList bounds;
		int boundsCount = split(*it, "-", &bounds);
		if (boundsCount == 1)
		{
			tmp[count++] = atoi(bounds.pop_back.c_str());
		}
		else if (boundsCount == 2)
		{
			int lower = atoi(bounds.pop_back().c_str());
			int upper = atoi(bounds.pop_back().c_str());
			for (int i = lower; i <= upper; i++)
			{
				tmp[count++] = i;
			}
		}
		else
		{
			return NULL;
		}
	}

	int out[count] = new int[count];
	for (int i = 0; i < count; i++)
		out[i] = tmp[i];

	return out;
}

bool validateCron(const element* const* cronElements)
{
    if ((!cronElements[CRON_MINUTE]->all && !(cronElements[CRON_MINUTE]->modulo > 0)) &&
        (cronElements[CRON_MINUTE]->list < 0) || (cronElements[CRON_MINUTE]->list > 59)) // list minute between 0 and 59
        return false;
	if ((!cronElements[CRON_HOUR]->all && !(cronElements[CRON_HOUR]->modulo > 0)) &&
        (cronElements[CRON_HOUR]->list < 0) || (cronElements[CRON_HOUR]->list > 23)) // list hour between 0 and 23
        return false;
    if ((!cronElements[CRON_DAY_OF_MONTH]->all && !(cronElements[CRON_DAY_OF_MONTH]->modulo > 0)) &&
        (cronElements[CRON_DAY_OF_MONTH]->list < 1) || (cronElements[CRON_DAY_OF_MONTH]->list > 31)) // list day of month between 1 and 31
        return false;
    if ((!cronElements[CRON_MONTH]->all && !(cronElements[CRON_MONTH]->modulo > 0)) &&
        (cronElements[CRON_MONTH]->list < 1) || (cronElements[CRON_MONTH]->list > 12)) // month between 1 and 12
        return false;
    if ((!cronElements[CRON_DAY_OF_WEEK]->all && !(cronElements[CRON_DAY_OF_WEEK]->modulo > 0)) &&
        (cronElements[CRON_DAY_OF_WEEK]->list < 0) || (cronElements[CRON_DAY_OF_WEEK]->list > 6)) // list day of week between 0 and 6
        return false;

	return (!(cronElements[CRON_MINUTE]->omit || cronElements[CRON_HOUR]->omit || cronElements[CRON_MONTH]->omit) && 
			 (cronElements[CRON_DAY_OF_WEEK]->omit ^ cronElements[CRON_DAY_OF_MONTH]->omit));
}

bool validateList(const int* list, int min, int max)
{
	for (int i = 0; i < countof(list); i++)
	{
		if ((list[i] < min) || (list[i] > max))
			return false;
	}
	
	return true;
}

bool isScheduledHour(int hour, const element* const* cronElements)
{
    return isScheduledValue(hour, cronElements[CRON_HOUR]);
}

bool isScheduledMinute(int minute, const element* const* cronElements)
{
	return isScheduledValue(minute, cronElements[CRON_MINUTE]);
}
bool isScheduledDayOfMonth(int dayOfMonth, const element* const* cronElements)
{
    return isScheduledValue(dayOfMonth, cronElements[CRON_DAY_OF_MONTH]);
}
bool isScheduledMonth(int month, const element* const* cronElements)
{
    return isScheduledValue(month, cronElements[CRON_MONTH]);
}
bool isScheduledDayOfWeek(int dayOfWeek, const element* const* cronElements)
{
    return isScheduledValue(dayOfWeek, cronElements[CRON_DAY_OF_WEEK]);
}

bool isScheduledValue(int value, const element* cronElement)
{
    if (cronElement->all)
        return true;
    else if (cronElement->modulo > 0)
        return ((value % cronElement->modulo) == 0);
    else
        return isValueInList(value, cronElement->list);
}

bool isValueInList(int value, const int* list)
{
	for (int i = 0; i < countof(list); i++)
	{
		if (list[i] == value)
			return true;
	}

	return false;
}

int split(const std::string str, const std::string delims, StringList* splits)
{
	int count = 0;
	int pos = 0;
	int last = 0;
	
	do
	{
		last = str.find_first_of(delims, pos);
		if (last != std::string::npos)
		{
			splits->push_back(str.substr(pos, last - pos));
			pos = last + 1;
			count++;
		}
	} while (last != std::string::npos);

	if (count == 0)
	{
		splits->push_back(str);
		count++;
	}
	else if (pos < str.size())
	{
		splits->push_back(str.substr(pos, str.size() - pos));
		count++;
	}
	
	return count;
}