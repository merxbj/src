#ifndef _CRON_SCHEDULE_PATTERN_H_
#define _CRON_SCHEDULE_PATTERN_H_

#pragma warning(disable: 4786)

#include <windows.h>
#include <vector>
#include <string>
#include <map>

typedef std::vector<std::string> Tokens;
typedef std::vector<int> ExplicitListOfValues;
typedef std::vector<int> ScheduleTimesType; // this is actually a redefinition from ScheduleCommon.h

///////////////////////////////////////////////////////////////////////
// Class:       CCronIncrement
// Description: Class representing the incremental format of single
//              schedule pattern element.
//              The supported format is 'A/B' where A represents the
//              initial value where the reccurence should start and
//              B represents the step.
//              For example 1/3 in minutes element wil expand to:
//              1, 4, 7, ..., 58, 1, 4 ...
//
class CCronIncrement
{
public:    
    int m_iStart;
    int m_iIncrement;

    CCronIncrement() : m_iStart(0), m_iIncrement(0) {}
    CCronIncrement(int iIncrement, int iStart = 0) : m_iIncrement(iIncrement), m_iStart(iStart) {}

    bool validate(int iMax);
};

///////////////////////////////////////////////////////////////////////
// Class:       CCronElement
// Description: Class representing a single part of the schedule 
//              pattern called 'element'.
//              Every element is aware of its position in the whole
//              pattern to be able to validate itself correctly.
//              Depending on its position it can be defined as:
//                  + '*'     - match for all possible values
//                  + 'A/B'   - see CCronIncrement class description
//                  + '?'     - this element will be omited (only valid
//                              for 'Day of Week' and 'Day of Month'
//                              with exclusive disjunction relation
//                              between each other.
//                  + 'X,A-B' - finally a list of explicit values
//
class CCronElement
{
public:
    int m_iPosition;
    CCronIncrement* m_increment;
    ExplicitListOfValues* m_list;
    bool m_bAll;
	bool m_bOmit;

    CCronElement(int iPos) : m_iPosition(iPos), m_increment(NULL), m_list(NULL), m_bAll(false), m_bOmit(true) {}
    CCronElement(int iPos, CCronIncrement* increment) : m_iPosition(iPos), m_increment(increment), m_list(NULL), m_bAll(false), m_bOmit(false) {}
    CCronElement(int iPos, bool bAll) : m_iPosition(iPos), m_increment(NULL), m_list(NULL), m_bAll(bAll), m_bOmit(false) {}
    CCronElement(int iPos, ExplicitListOfValues& list) : m_iPosition(iPos), m_increment(NULL), m_bAll(false), m_bOmit(false)
    {
        m_list = new ExplicitListOfValues();
        m_list->resize(list.size());
        std::copy(list.begin(), list.end(), m_list->begin());
    }

    ~CCronElement()
    {
        delete m_increment;
        delete m_list;
    }
    
    bool validate();
    bool validateList(const ExplicitListOfValues* list, int iMin, int iMax);
};

typedef std::vector<CCronElement*> CronElements;

///////////////////////////////////////////////////////////////////////
// Enum
// Description: Enumerates every single element in the whole schedule
//              pattern starting from seconds to the year.
//
enum
{
    CRON_SECOND,
    CRON_MINUTE,
	CRON_HOUR,
    CRON_DAY_OF_MONTH,
    CRON_MONTH,
    CRON_DAY_OF_WEEK,
    CRON_YEAR
};

///////////////////////////////////////////////////////////////////////
// Class:       CCronException
// Description: Simple class used as an exception being thrown if an
//              syntactic error is found during the schedule pattern
//              parse phase.
//
class CCronException
{
public:
    std::string m_strMessage;

    CCronException() : m_strMessage("") {}
    CCronException(std::string strMessage) : m_strMessage(strMessage) {}

    const char* toString() const { return m_strMessage.c_str(); }
};

///////////////////////////////////////////////////////////////////////
// Class:       CCronInternalException
// Description: Simple class used as an exception being thrown if an
//              unexpected error occures during the schedule pattern
//              parse phase. Such an error is probably caused by the
//              wrong semantic of parsing.
//
class CCronInternalException : public CCronException
{
public:
    CCronInternalException() : CCronException() {}
    CCronInternalException(std::string strMessage) : CCronException(strMessage) {}
};

///////////////////////////////////////////////////////////////////////
// Class:       CCRONSchedulePattern
// Description: Class encapsulating all the semantics of CRON Schedule
//              pattern parsing and generating the list of scheduled
//              times for a particular day.
//
class CCronSchedulePattern
{
public:
    CCronSchedulePattern(const std::string& strPattern);
    ~CCronSchedulePattern() {}

    int buildTodaySchedule(ScheduleTimesType* sttSchedule);
    int buildScheduleForDay(const SYSTEMTIME& stTimeToSchedule, ScheduleTimesType* sttSchedule);

private:
    void parse(CronElements* elements);
    CCronElement* parseSingleElement(const std::string& strRawElement, int iPos);
    int parseList(const std::string& strRawElement, ExplicitListOfValues* list);
    int parseListElement(const std::string& rawListElement);
    int buildSchedule(const CronElements& elements, ScheduleTimesType* sttSchedule);
    bool validate(const CronElements* pElements);
    bool isScheduledDay(const SYSTEMTIME& stTimeToSchedule, const CronElements& elements);
    bool isScheduledMinute(int iMinute, const CronElements& elements);
    bool isScheduledHour(int iHour, const CronElements& elements);
    bool isScheduledDayOfMonth(int iDayOfMonth, const CronElements& elements);
    bool isScheduledMonth(int iMonth, const CronElements& elements);
    bool isScheduledDayOfWeek(int iDayOfWeek, const CronElements& elements);
    bool isScheduledYear(int iYear, const CronElements& elements);
    bool isScheduledValue(int iValue, const CCronElement* element);
    bool isValueInList(int value, const ExplicitListOfValues* list);
    int split(const std::string& strDelimited, const std::string& strDelimiters, Tokens* tokens);
    void initMonthMap();
    void initDayMap();

private:
    std::map<std::string, int> m_monthMapping;
    std::map<std::string, int> m_dayMapping;

private:
    std::string m_strPattern;
};

#endif
