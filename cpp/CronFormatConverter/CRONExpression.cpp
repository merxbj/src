#include "CRONExpression.h"

///////////////////////////////////////////////////////////////////////
// Function:    Constructor
// Description: The sole contructor accepting the CRON Schedule Pattern
//              string.
// Parameters:  strPattern - The CRON Schedule Pattern string to be
//                           parsed and processed.
CCronSchedulePattern::CCronSchedulePattern(const std::string& strPattern)
{
    m_strPattern = strPattern;

    initMonthMap();
    initDayMap();
}

///////////////////////////////////////////////////////////////////////
// Function:    buildTodaySchedule
// Description: Function calculating the actual schedules times for
//              today. These times are returned in the given return 
//              paramamter.
// Parameters:  sttSchedule - The return parameter where the computed
//                            scheduled times are expected to be stored.
// Returns:     The number of computed today scheduled times.
//
int CCronSchedulePattern::buildTodaySchedule(ScheduleTimesType* sttSchedule)
{
    SYSTEMTIME stCurrTime;
    GetLocalTime(&stCurrTime);
    return buildScheduleForDay(stCurrTime, sttSchedule);
}

///////////////////////////////////////////////////////////////////////
// Function:    buildTodaySchedule
// Description: Function calculating the actual schedules times for
//              the given day. These times are returned in the given 
//              return paramamter.
// Parameters:  sttSchedule - The return parameter where the computed
//                            scheduled times are expected to be stored.
//              stTimeToSchedule - The day for the scheduled times are
//                                 going to be computed.
// Returns:     The number of computed today scheduled times.
//
int CCronSchedulePattern::buildScheduleForDay(const SYSTEMTIME& stTimeToSchedule, ScheduleTimesType* sttSchedule)
{
    int iScheduledTimes = 0;
    CronElements elements;

    parse(&elements);
    if (isScheduledDay(stTimeToSchedule, elements))
        iScheduledTimes = buildSchedule(elements, sttSchedule);

    for (CronElements::iterator it = elements.begin(); it != elements.end(); it++)
    {
        delete (*it);
    }
    elements.clear();
    
    return iScheduledTimes;
}

///////////////////////////////////////////////////////////////////////
// Function:    buildSchedule
// Description: Function doing the actual work of determing the
//              scheduled times based on the given already parsed 
//              elements.
// Parameters:  elements - The already parsed and validated pattern
//                         elements. These are going to determine
//                         the actual times being scheduled.
//              sttSchedule - The target vector where the scheduled
//                            times are going to be stored.
// Returns:     The number of computed scheduled times.
//
int CCronSchedulePattern::buildSchedule(const CronElements& elements, ScheduleTimesType* sttSchedule)
{
    for (int hour = 0; hour < 24; hour++)
    {
        if (isScheduledHour(hour, elements))
        {
            for (int minute = 0; minute < 60; minute++)
            {
                if (isScheduledMinute(minute, elements))
                {
                    sttSchedule->push_back((hour * 100) + minute);
                }
            }
        }
    }

    return sttSchedule->size();
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledDay
// Description: Function verifying whether there is a schedule
//              configured by the given already parsed schedule pattern 
//              for the given day.
// Parameters:  stTimeToSchedule - The day we are finding a schedule 
//                                 for.
//              elements - The already parsed and validated pattern
//                         elements.
// Returns:     true, if the given day matches the pattern, 
//              false otherwise.
//
bool CCronSchedulePattern::isScheduledDay(const SYSTEMTIME& stTimeToSchedule, const CronElements& elements)
{
    if (!isScheduledYear(stTimeToSchedule.wYear, elements))
		return false;
    
    if (!isScheduledMonth(stTimeToSchedule.wMonth, elements))
		return false;

	return (elements[CRON_DAY_OF_MONTH]->m_bOmit) ? 
		isScheduledDayOfWeek(stTimeToSchedule.wDayOfWeek, elements) : isScheduledDayOfMonth(stTimeToSchedule.wDay, elements);
}

///////////////////////////////////////////////////////////////////////
// Function:    parse
// Description: Function doing the hard work of parsing the schedule
//              pattern string representation (already passed to the
//              constructor).
//              It produces an vector of objective representation of
//              the elements string version which is finaly validated.
// Parameters:  elements - The target for the objective representation
//                         of the parsed elements.
//                         The elements are allocated on the heap, so
//                         they need to be freed by the consumer of 
//                         the parsed version.
//
void CCronSchedulePattern::parse(CronElements* elements)
{
    Tokens tokens;
    int n = split(m_strPattern, " ", &tokens);
    if ((n < 6) || (n > 7))
        throw CCronException("The number of elements must lie between 6 and 7 inclusive!");

    try
    {
        for (size_t i = 0; i < tokens.size(); i++)
        {
            CCronElement* element = parseSingleElement(tokens[i], i);
            if (element != NULL)
            {
                elements->push_back(element);
            }
            else
            {
                throw CCronException("Pattern element doesn't match to any expected format!");
            }
        }

        if (!validate(elements))
            throw CCronInternalException("Invalid pattern found without exception thrown!");
    }
    catch (const CCronException&)
    {
        for (CronElements::iterator it = elements->begin(); it != elements->end(); it++)
        {
            delete (*it);
        }
        elements->clear();

        throw;
    }
}

///////////////////////////////////////////////////////////////////////
// Function:    parseSingleElement
// Description: Function parsing a single string representation of the 
//              given element.
//              It determines the format being used and creates the 
//              appropriate objective representation of it.
// Parameters:  strRawElement - The raw (string) representation of the
//                              element to be parsed.
//              iPos - The actual position of the element in the original
//                     pattern as the objective representation needs to
//                     be aware of it to validate itself properly.
// Returns:     The actual objective representation of the given parsed
//              element.
//              NULL if the format cannot be determined.
//
CCronElement* CCronSchedulePattern::parseSingleElement(const std::string& strRawElement, int iPos)
{
    if (strRawElement.compare("?") == 0)
    {
        return new CCronElement(iPos);
    }
    else if (strRawElement.compare("*") == 0)
    {
        return new CCronElement(iPos, true);
    }
    else if (strRawElement.find_first_of("/") != std::string::npos)
    {
        Tokens incrementTokens;
        int numTokens = split(strRawElement, "/", &incrementTokens);

        if (numTokens != 2)
            throw CCronException("Invalid format of incremental schedule step definition!");

        int iStart = atoi(incrementTokens[0].c_str());
        int iIncrement = atoi(incrementTokens[1].c_str());

        if ((iIncrement <= 0) && (iStart < 0))
            throw CCronException("Invalid format of incremental schedule step definition!");

        CCronIncrement* increment = new CCronIncrement(iIncrement, iStart);
        return new CCronElement(iPos, increment);
    }
    else
    {
        ExplicitListOfValues list;

        if (parseList(strRawElement, &list) <= 0)
            throw CCronException("List of values assumed but parsed no elements.");

        return new CCronElement(iPos, list);
    }
}

///////////////////////////////////////////////////////////////////////
// Function:    parseList
// Description: Function parsing the explicit list of values.
//              It can be a single value or list of values delimited by
//              a comma or finaly a range of values specified by the
//              lower and upper bound separated by a hyphen.
//              The expected result is a vector filled by all the values
//              matching the given raw string list representation.
// Parameters:  strRawElement - The raw representation of the schedule
//                              pattern element which was assumed to be
//                              an explicit list of values.
//              list - The target for the parsed list of values.
//
// Returns:     The actual number of values defined by the given list.
//
int CCronSchedulePattern::parseList(const std::string& strRawElement, ExplicitListOfValues* list)
{
    Tokens listElements;
    split(strRawElement, ",", &listElements);

    for (Tokens::const_iterator it = listElements.begin(); it != listElements.end(); it++)
    {
        std::string listElement = (*it);
        if (listElement.find_first_of("-") != std::string::npos)
        {
            Tokens rangeBounds;
            split(listElement, "-", &rangeBounds);

            if (rangeBounds.size() != 2)
                throw CCronException("Invalid list elements range specification!");

            int iLowerBound = parseListElement(rangeBounds[0]);
            int iUpperBound = parseListElement(rangeBounds[1]);

            if (iLowerBound > iUpperBound)
                throw CCronException("Invalid list elements range specification! Lower bound is greater than upper bound");

            for (int i = iLowerBound; i <= iUpperBound; i++)
            {
                list->push_back(i);
            }
        }
        else
        {
            list->push_back(parseListElement(listElement));
        }
    }

    return list->size();
}

///////////////////////////////////////////////////////////////////////
// Function:    parseListElement
// Description: Parses a single raw string representation of list
//              element.
//              It can be a number or a short name of month or day
//              (jun, oct or mon, sat).
// Parameters:  rawListElement - The string representation of the single
//                               list element.
//
// Returns:     The parsed numercial representation of the given single
//              list element.
//              The month/day short names are going to be represented
//              according to their natural order.
//
int CCronSchedulePattern::parseListElement(const std::string& rawListElement)
{
    if (m_monthMapping.count(rawListElement) == 1)
    {
        return m_monthMapping[rawListElement];
    }
    else if (m_dayMapping.count(rawListElement) == 1)
    {
        return m_dayMapping[rawListElement];
    }

    return atoi(rawListElement.c_str());
}

///////////////////////////////////////////////////////////////////////
// Function:    validate
// Description: Function validating the syntactic format of the
//              schedule pattern.
//              It means that all the element values must fit to their
//              constraints and all the CRON specific syntactic rules
//              must be passed (either DOW or DOM must be omited)
// Parameters:  pElements - The parsed schedule elements to be validated.
//
// Returns:     true if the schedule pattern is valid, false otherwise.
//
bool CCronSchedulePattern::validate(const CronElements* pElements)
{
    const CronElements elements = (*pElements);

    for (size_t i = 0; i < elements.size(); i++)
    {
        elements[i]->validate(); // tell the element on what position it is to validate values properly
    }
    
    if (!(elements[CRON_DAY_OF_WEEK]->m_bOmit ^ elements[CRON_DAY_OF_MONTH]->m_bOmit))
        throw CCronException("One of the fourth and sixth elements (day of week and day of month) must be omited ('?')!");

	return true;
}

///////////////////////////////////////////////////////////////////////
// Function:    initMonthMap
// Description: Initializes the map mapping the short month names to 
//              their actual number representing the order in the year.
//
void CCronSchedulePattern::initMonthMap()
{
    m_monthMapping["jan"] = 1;
    m_monthMapping["feb"] = 2;
    m_monthMapping["mar"] = 3;
    m_monthMapping["apr"] = 4;
    m_monthMapping["may"] = 5;
    m_monthMapping["jun"] = 6;
    m_monthMapping["jul"] = 7;
    m_monthMapping["aug"] = 8;
    m_monthMapping["sep"] = 9;
    m_monthMapping["oct"] = 10;
    m_monthMapping["nov"] = 11;
    m_monthMapping["dec"] = 12;
}

///////////////////////////////////////////////////////////////////////
// Function:    initDayMap
// Description: Initializes the map mapping the short day names to 
//              their actual number representing the order in the week
//              startin on Sunday.
//
void CCronSchedulePattern::initDayMap()
{
    m_dayMapping["sun"] = 1;
    m_dayMapping["mon"] = 2;
    m_dayMapping["tue"] = 3;
    m_dayMapping["wed"] = 4;
    m_dayMapping["thu"] = 5;
    m_dayMapping["fri"] = 6;
    m_dayMapping["sat"] = 7;
}

///////////////////////////////////////////////////////////////////////
// Function:    split
// Description: Helper function doing the actual work of spliting the
//              given string by the given delimiters.
//              The splited version is then returned in the given return
//              parameter.
// Parameters:  strDelimited - The string to be splited.
//              strDelimiters - Delimiters which should split the
//                              given string to tokens.
//              tokens - The target of the split where the found tokens
//                       are going to be stored.
// Returns:     The number of found tokens.
//
int CCronSchedulePattern::split(const std::string& strDelimited, const std::string& strDelimiters, Tokens* tokens)
{
    std::string::size_type pos = 0;
    std::string::size_type lastPos = 0;

    pos = strDelimited.find_first_of(strDelimiters);
    while (pos != std::string::npos)
    {
        tokens->push_back(strDelimited.substr(lastPos, pos - lastPos));

        lastPos = pos + 1;
        pos = strDelimited.find_first_of(strDelimiters, lastPos);
    }

    if (lastPos != pos)
    {
        pos = strDelimited.length();
        tokens->push_back(strDelimited.substr(lastPos, pos - lastPos));
    }

    return tokens->size();
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledMinute
// Description: Verifies whether the given minute matches the given
//              parsed schedule pattern.
// Parameters:  iMinute - The minute to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given minute matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledMinute(int iMinute, const CronElements& elements)
{
	return isScheduledValue(iMinute, elements[CRON_MINUTE]);
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledHour
// Description: Verifies whether the given hour matches the given
//              parsed schedule pattern.
// Parameters:  iHour - The hour to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given hour matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledHour(int iHour, const CronElements& elements)
{
    return isScheduledValue(iHour, elements[CRON_HOUR]);
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledDayOfMonth
// Description: Verifies whether the given day of month matches the given
//              parsed schedule pattern.
// Parameters:  iDayOfMonth - The day of month to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given day of month matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledDayOfMonth(int iDayOfMonth, const CronElements& elements)
{
    return isScheduledValue(iDayOfMonth, elements[CRON_DAY_OF_MONTH]);
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledMonth
// Description: Verifies whether the given month matches the given
//              parsed schedule pattern.
// Parameters:  iMonth - The month to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given month matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledMonth(int iMonth, const CronElements& elements)
{
    return isScheduledValue(iMonth, elements[CRON_MONTH]);
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledDayOfWeek
// Description: Verifies whether the given minute matches the given
//              parsed schedule pattern.
// Parameters:  iDayOfWeek - The day of week to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given day of week matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledDayOfWeek(int iDayOfWeek, const CronElements& elements)
{
    return isScheduledValue(iDayOfWeek, elements[CRON_DAY_OF_WEEK]);
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledYear
// Description: Verifies whether the given year matches the given
//              parsed schedule pattern.
//              The year element is by definition optional. Therefore,
//              it is checked whether it is present, at first.
//              If its not, it is considered as being '*', hence
//              everything is matching.
// Parameters:  iYear - The year to be matched.
//              elements - The parsed schedule pattern.
// Returns:     true if the given year matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledYear(int iYear, const CronElements& elements)
{
    return (elements.size() == 7) ? isScheduledValue(iYear, elements[CRON_YEAR]) : true;
}

///////////////////////////////////////////////////////////////////////
// Function:    isScheduledValue
// Description: Verifies whether the given value matches the given
//              objective representation of the schedule pattern.
//              This involves:
//                  + If the '*' was choosed, it matches
//                  + If the 'A/B' was choosed, it verifies whether
//                    the starting value (A) was already passed and then
//                    whether the given value is an increment according
//                    the configured step (B)
//                  + If the explicit list was choosed, it delegates
//                    the verification to the specialized function.
//                    See the isValueInList description.
// Parameters:  iValue - The value to be matched.
//              element - The parsed schedule pattern single element.
// Returns:     true if the given value matches the given pattern,
//              false otherwise
//
bool CCronSchedulePattern::isScheduledValue(int iValue, const CCronElement* element)
{
    if (element->m_bAll)
        return true;
    else if (element->m_increment != NULL)
        return ((iValue >= element->m_increment->m_iStart) && 
                ((iValue - element->m_increment->m_iStart) % element->m_increment->m_iIncrement) == 0);
    else
        return isValueInList(iValue, element->m_list);
}

///////////////////////////////////////////////////////////////////////
// Function:    isValueInList
// Description: Verifies whether the given value matches the given
//              list of values (i.e. whether the value is in the list)
//              simply by iterating through that list and waiting for
//              match.
// Parameters:  iValue - The value to be matched.
//              list - The list of values where we are looking for the
//                     given value.
// Returns:     true if the given list contains the given value,
//              false otherwise.
//
bool CCronSchedulePattern::isValueInList(int iValue, const ExplicitListOfValues* list)
{
    for (ExplicitListOfValues::const_iterator it = list->begin(); it != list->end(); it++)
	{
		if ((*it) == iValue)
			return true;
	}

	return false;
}

///////////////////////////////////////////////////////////////////////
// Function:    validate
// Description: Validates this element whether the values matches its 
//              constraints (based on the known element position). 
//              It involves:
//                  + If it is a star '*' it matches
//                  + If the '?' is correctly used it matches
//                  + If the 'A/B' or the list falls between the
//                    value boundaries (e.g. for the first element
//                    it is 0 - 59 seconds) it matches
// Returns:     true if this is a valid element, false otherwise.
//
bool CCronElement::validate()
{
    // if the element is simple '*' there is nothing to validate at all
    if (m_bAll)
        return true;

    // prepare the valid values depending on the element position
    int iMinValue = 0;
    int iMaxValue = 2099;
    bool bOmitAllowed = false;

    switch (m_iPosition)
    {
    case CRON_SECOND:
    case CRON_MINUTE:
        iMinValue = 0;
        iMaxValue = 59;
        bOmitAllowed = false;
        break;
    case CRON_HOUR:
        iMinValue = 0;
        iMaxValue = 23;
        bOmitAllowed = false;
        break;
    case CRON_DAY_OF_MONTH:
        iMinValue = 1;
        iMaxValue = 31;
        bOmitAllowed = true;
        break;
    case CRON_MONTH:
        iMinValue = 1;
        iMaxValue = 12;
        bOmitAllowed = false;
        break;
    case CRON_DAY_OF_WEEK:
        iMinValue = 1;
        iMaxValue = 7;
        bOmitAllowed = true;
        break;
    case CRON_YEAR:
        iMinValue = 1970;
        iMaxValue = 2099;
        bOmitAllowed = false;
        break;
    default:
        throw CCronInternalException("Unexpected element position during validation!");
    }

    // do the actual validation
    if (m_bOmit && bOmitAllowed)
        return true;
    else if (m_bOmit && !bOmitAllowed)
        throw CCronException("Element ommited by '?' although it is not allowed to!");
    else if (m_increment != NULL)
        if (!m_increment->validate(iMaxValue))
            throw CCronException("Increment syntax used (A/B) but it exceedes the maximum value so it never hits!");
        else 
            return true;
    else if (m_list != NULL) 
        if (!validateList(m_list, iMinValue, iMaxValue))
            throw CCronException("The explicit list assumed but they don't fit the boundary values!");
        else 
            return true;
    else
        throw CCronInternalException("Unable to determine the element value syntax to validate it!");
}

///////////////////////////////////////////////////////////////////////
// Function:    validateList
// Description: Function validates the given list againts the given
//              boundary values.
//              Every value in the list must fall between the given
//              min and max value.
// Parameters:  list - The list to be validated.
//              iMin - The lower bound.
//              iMax - The upper bound.
//
// Returns:     true if all elements in the list matches the given
//              constraint, false otherwise.
//
bool CCronElement::validateList(const ExplicitListOfValues* list, int iMin, int iMax)
{
    for (ExplicitListOfValues::const_iterator it = list->begin(); it != list->end(); it++)
    {
        int element = (*it);
        if ((element < iMin) || (element > iMax))
            return false;
    }
    
    return true;
}

///////////////////////////////////////////////////////////////////////
// Function:    validate
// Description: Function validates this increment whether it matches
//              the given contraint.
//              This involves:
//                  + The starting value must not be greater than
//                    the given maximum value.
//                  + The step must not be greater than the given 
//                    maximum value
//              Actually, this won't be an issue, if the increment would
//              exceed the maximum value, however it would never match,
//              that doesn't make sense and therefore is safer to 
//              notify the user that there is probably invalid pattern
//              used to schedule.
// Parameters:  iMax - The upper bound.
//
// Returns:     true if this is a valid increment, false otherwise.
//
bool CCronIncrement::validate(int iMax)
{
    if ((m_iStart > iMax) || (m_iIncrement > iMax))
        return false;

    return true;
}