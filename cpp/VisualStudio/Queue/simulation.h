#ifndef _simulation_h_
#define _simulation_h_
#pragma once

#include <cmath>
#include <ctime>
enum
{
    STATUS_OK = 0,
    WRONG_INPUT = 1,
    DIVISION_BY_ZERO = 2
};
    
class CResults
{

private:
    
    unsigned long m_refusedCustomers;
    unsigned long m_acceptedCustomers;
    double m_avarageWaitTime;

public:
    
    CResults( unsigned long refusedCustomers = 0,
              unsigned long acceptedCustomers = 0, 
              double avarageWaitTime = 0 );
    void AddRefusedCustomers( unsigned int customers = 1);
    void AddAcceptedCustomers( unsigned int customers = 1);
    unsigned int FigureAvarageTime( unsigned long waitTime );
    void Print();
    void Clear();

};

class CSimulation
{

private:

    unsigned int m_maxCustomers;
    unsigned int m_customersPerHour;
    unsigned long m_simulationDuration;
    unsigned long m_waitTime; // wait time of all customers

    unsigned int SetMaxCustomers( unsigned int maxCustomers );
    unsigned int GetMaxCustomers();
    unsigned int SetCustomersPerHour( unsigned int customersPerHour );
    unsigned int GetCustomersPerHour();
    unsigned int SetSimulationDuration( unsigned long simulationDuration );
    unsigned long GetSimulationDuration();
    
    void GetParameters();
    void BeginSimulation( CResults * results ); 

    bool NewCustomer( unsigned int customersProcessed, unsigned long minutes, unsigned int customersActualMinute );
    CCustomer * DispatchCustomer( CQueue * queue );
    unsigned long GetWaitTime( CQueue * queue ) const;
    void UpdateWaitTimes( CQueue * queue );

public:
    
    CSimulation( unsigned int maxCustomers = 0,
                 unsigned int customersPerHour = 0,
                 unsigned int simulationDuration = 0,
                 unsigned long waitTime = 0 );
    void Prepare();

};

#endif