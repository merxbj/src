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

int buildTodaySchedule(const char* cron, int* schedule);
bool isScheduledDay(const char* cron);
int buildSchedule(const char* cron, int* schedule);
bool parseCron(const char* cron, element** cronElements);
element* parseSingleCronElement(const char* rawCronElement);
bool validateCron(const element const* const* cronElements);

int main()
{
    const char cron[] = "* * * * *";
    int* schedule;

    int count = buildTodaySchedule(cron, schedule);

    for (int i = 0; i < count; i++)
        printf("%2d:%2d", schedule[i] / 100, schedule[i] % 100);

    return 0;
}

int buildTodaySchedule(const char* cron, int* schedule)
{
    if (isScheduledDay(cron))
        return buildSchedule(cron, schedule);
    else
        return 0;
}

bool isScheduledDay(const char* cron)
{
    return true;
}

int buildSchedule(const char* cron, int* schedule)
{
    element** cronElements = new element*[5];
    if (parseCron(cron, cronElements))
    {
        return 0;
    }
    else
    {
        return 0;
    }
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
            for (; i >= 0; i--)
                delete cronElements[i];
            return false;
        }
        
        token = strtok(NULL, delimiter);
    }

    return ((i == 4) && validateCron(cronElements));
}

element* parseSingleCronElement(const char* rawCronElement)
{
    if (strcmp(rawCronElement, "*"))
    {
        return new element();
    }
    else if (strstr(rawCronElement, "*/") != NULL)
    {
        int modulo = atoi(rawCronElement + 2);
        if (modulo > 0)
            return new element(modulo, 0, false);
    }
    else
    {
        int exact = atoi(rawCronElement);
        if ((exact < 60) && (exact >= 0))
            return new element (0, exact, false);
    }

    return NULL;
}

bool validateCron(const element const* const* cronElements)
{
    if ((cronElements[0]->exact < 0) || (cronElements[0]->exact > 59)) // minute
        return false;
    if ((cronElements[1]->exact < 0) || (cronElements[1]->exact > 23)) // hour
        return false;
    if ((cronElements[2]->exact < 1) || (cronElements[2]->exact > 31)) // day of month
        return false;
    if ((cronElements[3]->exact < 1) || (cronElements[3]->exact > 12)) // month
        return false;
    if ((cronElements[4]->exact < 0) || (cronElements[4]->exact > 6)) // day if week
        return false;

    return true;
}