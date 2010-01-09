#include "windows.h"
#include <iostream>
using namespace std;

typedef struct element_t
{
    int modulo;
    int exact;
    bool all;

    element_t() : modulo(0), exact(0), all(true) {}
    element_t(int _modulo, int _exact, bool _all) : modulo(_modulo), exact(_exact), all(_all) {}
} element;

const char* months = "janfebmaraprmayjunjulaugsepoctnovdec";
const char* days = "sunmontuewedthufrisat";

enum
{
    CRON_HOUR,
    CRON_MINUTE,
    CRON_DAY_OF_MONTH,
    CRON_MONTH,
    CRON_DAY_OF_WEEK
};

int buildTodaySchedule(const char* cron, int** schedule);
bool isScheduledDay(const element* const* cronElements);
int buildSchedule(const element* const* cronElements, int** schedule);
bool parseCron(const char* cron, element** cronElements);
element* parseSingleCronElement(const char* rawCronElement);
bool validateCron(const element* const* cronElements);
bool isScheduledHour(int hour, const element* const* cronElements);
bool isScheduledMinute(int minute, const element* const* cronElements);
bool isScheduledValue(int value, const element* cronElement);
bool isScheduledDayOfMonth(int dayOfMonth, const element* const* cronElements);
bool isScheduledMonth(int month, const element* const* cronElements);
bool isScheduledDayOfWeek(int dayOfWeek, const element* const* cronElements);

int main()
{
    const char cron[] = "*/2 30 10 1 0";
    int* schedule = NULL;

    int count = buildTodaySchedule(cron, &schedule);

    for (int i = 0; i < count; i++)
        printf("%02d:%02d\n", schedule[i] / 100, schedule[i] % 100);

    system("PAUSE");
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

    return ((isScheduledDayOfMonth(stCurrTime.wDay, cronElements)) &&
            (isScheduledMonth(stCurrTime.wMonth, cronElements)) &&
            (isScheduledDayOfWeek(stCurrTime.wDayOfWeek, cronElements)));
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
    else if (strstr(rawCronElement, "*/") != NULL)
    {
        int modulo = atoi(rawCronElement + 2);
        if (modulo > 0)
            return new element(modulo, 0, false);
    }
    else if ((month = strstr(months, rawCronElement)) != NULL)
    {
        int pos = month - months;
        if (pos % 3 == 0)
            return new element(0, (pos / 3) + 1, false);
    }
    else if ((day = strstr(days, rawCronElement)) != NULL)
    {
        int pos = day - days;
        if (pos % 3 == 0)
            return new element(0, pos / 3, false);
    }
    else
    {
        int exact = atoi(rawCronElement);
        if ((exact < 60) && (exact >= 0))
            return new element (0, exact, false);
    }

    return NULL;
}

bool validateCron(const element* const* cronElements)
{
    if ((!cronElements[CRON_HOUR]->all && !(cronElements[CRON_HOUR]->modulo > 0)) &&
        (cronElements[CRON_HOUR]->exact < 0) || (cronElements[CRON_HOUR]->exact > 23)) // minute
        return false;
    if ((!cronElements[CRON_MINUTE]->all && !(cronElements[CRON_MINUTE]->modulo > 0)) &&
        (cronElements[CRON_MINUTE]->exact < 0) || (cronElements[CRON_MINUTE]->exact > 59)) // hour
        return false;
    if ((!cronElements[CRON_DAY_OF_MONTH]->all && !(cronElements[CRON_DAY_OF_MONTH]->modulo > 0)) &&
        (cronElements[CRON_DAY_OF_MONTH]->exact < 1) || (cronElements[CRON_DAY_OF_MONTH]->exact > 31)) // day of month
        return false;
    if ((!cronElements[CRON_MONTH]->all && !(cronElements[CRON_MONTH]->modulo > 0)) &&
        (cronElements[CRON_MONTH]->exact < 1) || (cronElements[CRON_MONTH]->exact > 12)) // month
        return false;
    if ((!cronElements[CRON_DAY_OF_WEEK]->all && !(cronElements[CRON_DAY_OF_WEEK]->modulo > 0)) &&
        (cronElements[CRON_DAY_OF_WEEK]->exact < 0) || (cronElements[CRON_DAY_OF_WEEK]->exact > 6)) // day if week
        return false;

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
        return (value == cronElement->exact);
}