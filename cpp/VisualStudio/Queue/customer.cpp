#include "queue.h"

CCustomer::CCustomer( const char * newName, const short newWaitTime )
{
    char * tempName = new char [ strlen( newName ) + 1 ];
    strcpy(tempName,newName);
    m_name = tempName;

    m_waitTime = newWaitTime;
    m_doTime = (rand() % 3) + 1;
}

CCustomer::~CCustomer()
{   
    delete [] m_name;
}

char * CCustomer::GetName() const
{
    if ( m_name != NULL ) { return m_name; }
    else 
    { 
        return ""; 
    }
}

short CCustomer::GetWaitTime() const
{
    return m_waitTime;
}

void CCustomer::SetWaitTime( short newWaitTime )
{
    m_waitTime = newWaitTime;
}

std::ostream & operator<<( std::ostream & os, const CCustomer & c )
{
    os << c.GetName() <<std::endl;
    return os;
}

short CCustomer::GetDoTime() const
{
    return m_doTime;
}

void CCustomer::SetDoTime( short doTime )
{
    m_doTime = doTime;
}