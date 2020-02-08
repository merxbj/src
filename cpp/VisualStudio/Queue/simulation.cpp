#include "queue.h"

CSimulation::CSimulation( unsigned int maxCustomers,
                          unsigned int customersPerHour,
                          unsigned int simulationDuration,
                          unsigned long waitTime) : m_maxCustomers( maxCustomers ),
                                                    m_customersPerHour( customersPerHour ),
                                                    m_simulationDuration( simulationDuration ),
                                                    m_waitTime( waitTime )
{
};

void CSimulation::Prepare()
{
    std::cout << "Welcome in CSimulate object of simulation of cash dispenser." << std::endl;
    std::cout << "For termination, please, press 'q', otherwise, press any key." << std::endl;

    CResults * results = new CResults();
    
    char ch;
    std::cin >> ch;
    while ( ch != 'q' )    
    {        
        results->Clear();
        GetParameters();
        BeginSimulation( results );
        results->Print();
        std::cout << "For termination, please, press 'q', otherwise, press any key." << std::endl;
        std::cin >> ch;
    }

    std::cout << "CSimulate say you good bye :)" << std::endl;
    
    delete results;
}

void CSimulation::GetParameters()
{
    unsigned int status;

#ifndef _DEBUGING_

    do
    {
        std::cout << "Enter maximal lenght of queue: ";
        unsigned int maxCustomers;
        std::cin >> maxCustomers;
        status = SetMaxCustomers( maxCustomers );
    } while ( status != STATUS_OK );  

    do
    {
        std::cout << "\nEnter avarage customers per hour: ";
        double customersPerHour;
        std::cin >> customersPerHour;
        status = SetCustomersPerHour( customersPerHour );
    } while ( status != STATUS_OK );

    do
    {
        std::cout << "\nEnter duration of simulation: ";
        double simulationDuration;
        std::cin >> simulationDuration;
        status = SetSimulationDuration( simulationDuration );
    } while ( status != STATUS_OK );

#else

    status = SetMaxCustomers( 10 );
    status = SetCustomersPerHour( 20 );
    status = SetSimulationDuration( 48 );

#endif
}

void CSimulation::BeginSimulation( CResults * results )
{
    srand(rand());
    CQueue * queue = new CQueue( 0, 0, m_maxCustomers );
    int customersActualHour = 0;
    int minutesActualHour = 0;
    long waitTime = 0;
    
    unsigned long index = 0;
    while ( index < ( GetSimulationDuration() * 60 ) )
    {
        UpdateWaitTimes( queue );
        CCustomer * dispatchedCustomer = 0;
        dispatchedCustomer = DispatchCustomer( queue );
        if ( dispatchedCustomer )
        {
            dispatchedCustomer = queue->RemoveFromQueue();
            waitTime += dispatchedCustomer->GetWaitTime();
#if defined ( _LOGING_ )
            std::cout << "DISPATCHED USER! HE WAITED: " << dispatchedCustomer->GetWaitTime() << std::endl;
#endif
            delete dispatchedCustomer;
        }

        int customersActualMinute = 0;
        while ( NewCustomer( customersActualHour, minutesActualHour, customersActualMinute ) )
        {
            customersActualHour++;
            customersActualMinute++;
            if ( !queue->IsFull() )
            {
                CCustomer * customer = new CCustomer();
                queue->AddToQueue(*customer);                
                results->AddAcceptedCustomers();
            }
            else
            {
#if defined ( _LOGING_ )
                std::cout << "POOR CUSTOMER, QUEUE IS NOW FULL. CUSTOMERS = " << queue->NumberOfItems() << std::endl;
#endif
                results->AddRefusedCustomers();
            }
        }
#if defined ( _LOGING_ )
        std::cout << "==================== END OF MINUTE ====================" << std::endl;
        system( "PAUSE" );
#endif
        index += 1;
        minutesActualHour = minutesActualHour < 59 ? minutesActualHour + 1 : 0;
        customersActualHour = minutesActualHour < 59 ? customersActualHour : 0;
    }

    if ( !queue->IsEmpty() )
    {
        waitTime += GetWaitTime(queue);
    }

    results->FigureAvarageTime( waitTime );

    delete [] queue;
}

bool CSimulation::NewCustomer( unsigned int customersActualHour, 
                               unsigned long minutesActualHour, 
                               unsigned int customersActualMinute )
{   
    // how many customers should come in this actual hour
    int customersLeftActualHour = m_customersPerHour - customersActualHour;
    // depending on customers already accepted and left, evaluate possibility of new customer to come
    double possibilityOfNewCustomer = customersLeftActualHour / ( 60.0-minutesActualHour );
    // there is a possibility of income of more customers in one minute, but more customers will decrease
    // a possibility of next customer to come in same minute
    possibilityOfNewCustomer -= ( 0.33 * customersActualMinute );
    // make a random number from 0 to 1 
    double newTry = static_cast<double>( rand() % 101 ) / 100;
#if defined ( _LOGING_ )
    std::cout << "LOG: POSSIBILITY = " << possibilityOfNewCustomer;
    std::cout << " TRY = " << newTry << std::endl;
#endif
    // check wheter the customer come
    if ( newTry <= possibilityOfNewCustomer )
    {
        return true;
    }
    // or not
    else
    {
        return false;
        
    }
}

unsigned int CSimulation::GetCustomersPerHour()
{
    return m_customersPerHour;
}

unsigned int CSimulation::GetMaxCustomers()
{
    return m_maxCustomers;
}

unsigned long CSimulation::GetSimulationDuration()
{
    return m_simulationDuration;
}

unsigned int CSimulation::SetCustomersPerHour( unsigned int customersPerHour )
{
    if ( customersPerHour > 0 )
    {
        m_customersPerHour = customersPerHour;
        return STATUS_OK;
    }
    else
    {
        return WRONG_INPUT;
    }
}

unsigned int CSimulation::SetMaxCustomers( unsigned int maxCustomers )
{
    if ( maxCustomers > 0 )
    {
        m_maxCustomers = maxCustomers;
        return STATUS_OK;
    }
    else
    {
        return WRONG_INPUT;
    }
}

unsigned int CSimulation::SetSimulationDuration( unsigned long simulationDuration )
{
    if ( simulationDuration > 0 )
    {
        m_simulationDuration = simulationDuration;
        return STATUS_OK;
    }
    else
    {
        return WRONG_INPUT;
    }
}

CResults::CResults(unsigned long refusedCustomers,
                   unsigned long acceptedCustomers,
                   double avarageWaitTime) : m_refusedCustomers ( refusedCustomers ),
                                             m_acceptedCustomers ( acceptedCustomers ),
                                             m_avarageWaitTime ( m_avarageWaitTime )
{
}

void CResults::AddAcceptedCustomers( unsigned int customers )
{
    m_acceptedCustomers += customers;
}

void CResults::AddRefusedCustomers( unsigned int customers )
{
    m_refusedCustomers += customers;
}

void CResults::Clear()
{
    m_refusedCustomers = 0;
    m_acceptedCustomers = 0;
    m_avarageWaitTime = 0;
}

unsigned int CResults::FigureAvarageTime( unsigned long waitTime )
{
    if ( m_acceptedCustomers != 0 )
    {
        m_avarageWaitTime = static_cast<double>(waitTime) / m_acceptedCustomers;
        return STATUS_OK;
    }
    else
    {
        return DIVISION_BY_ZERO;
    }
}

void CResults::Print()
{    
    std::cout << "Results of last simulation" << std::endl;
    std::cout << "--------------------------------------------------------------" << std::endl;
    std::cout << "Customers wanted to use cash dipenser: " << m_acceptedCustomers + m_refusedCustomers << std::endl;
    std::cout << "Customers accepted in queue: " << m_acceptedCustomers << std::endl;
    std::cout << "Customers refused from queue: " << m_refusedCustomers << std::endl;
    std::cout << "Avarage wait time in queue: " << m_avarageWaitTime << std::endl;
}

CCustomer * CSimulation::DispatchCustomer( CQueue * queue )
{
    CCustomer *customer;
    queue->ResetPointer();
    customer = queue->GetCustomer();
    
    if ( customer )
    {
        if ( customer->GetDoTime() == 0 )
        {
            return customer;
        }
        else
        {
            customer->SetDoTime( customer->GetDoTime() - 1 );
        }
    }
    return 0;
}

unsigned long CSimulation::GetWaitTime(CQueue * queue) const
{
    queue->ResetPointer();
    unsigned long sumWaitTime = 0;

    CCustomer * customer = queue->GetCustomer();
    while ( customer )
    {
        sumWaitTime += customer->GetWaitTime();
        queue->MovePointerForward();
        customer = queue->GetCustomer();
    }
    return sumWaitTime;
}

void CSimulation::UpdateWaitTimes( CQueue * queue )
{
    queue->ResetPointer();

    CCustomer * customer = queue->GetCustomer();
    while ( customer )
    {
        customer->SetWaitTime( customer->GetWaitTime() + 1 );
        queue->MovePointerForward();
        customer = queue->GetCustomer();
    }
}