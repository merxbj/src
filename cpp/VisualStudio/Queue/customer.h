#ifndef _customer_h_
#define _customer_h_
#pragma once

#include "queue.h"

class CCustomer
{

private:

    char * m_name;
    short m_waitTime;
    short m_doTime;
    friend std::ostream & operator<<( std::ostream & os, const CCustomer & c ); 

public:

    CCustomer( const char * newName = "unnamed",const short newWaitTime = 0 );
    ~CCustomer();
    char * GetName() const;
    short GetWaitTime() const;
    void SetWaitTime( short waitTime );
    short GetDoTime() const;
    void SetDoTime( short doTime );
};

#endif