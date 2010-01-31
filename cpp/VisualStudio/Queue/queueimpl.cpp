#include "queue.h"

CQueue::CQueue( Node * newFirst, Node * newLast, const unsigned int maxItems ) : m_MAX_ITEMS( maxItems ) 
{     
    m_first = newFirst;
    if ( newFirst != NULL && newLast == NULL ) 
    { 
        m_last = m_first; 
    }
    else 
    { 
        m_first = newLast; 
    }

    unsigned int count = 0;
    Node * node = m_first;
    if ( m_first != NULL )
    {

        while ( node->next != NULL )
            count++;

        m_numberOfItems = count + 1;
    }
    else 
    { 
        m_numberOfItems = 0; 
    }

}

bool CQueue::AddToQueue( const CCustomer & newCustomer )
{

    if ( !IsFull() )
    {
        Node * node = new Node;         

        node->next = NULL;
        node->previous = m_last;

        if ( !IsEmpty() )
        {
            m_last->next = node;
        }         
        else if ( IsEmpty() ) 
        { 
            m_first = node; 
        }         
        m_last = node;

        node->item = &newCustomer;

        m_numberOfItems += 1;

        return true;
    }

    else 
    { 
        return false; 
    }

}

CCustomer * CQueue::RemoveFromQueue()
{
    if ( !IsEmpty() )
    {
        Node * oldItem = m_first;
        CCustomer * oldCustomer = const_cast<CCustomer*>(oldItem->item);
        oldItem->item = NULL;

        m_first = m_first->next;
        if ( m_first != NULL )
        {
            m_first->previous = NULL;
        }

        m_numberOfItems -= 1;
        delete oldItem;

        return oldCustomer;
    }
    else
    {
        return NULL;
    }
}

unsigned int CQueue::NumberOfItems() const
{
    return m_numberOfItems;
}


bool CQueue::IsEmpty() const
{
    return m_numberOfItems == 0;
}

bool CQueue :: IsFull() const
{
    return m_numberOfItems == m_MAX_ITEMS;
}

void CQueue::PrintList() const
{
    Node * node = m_first;

    if ( !IsEmpty() )
    {
        std::cout << "----------- VYPIS ----------------" << std::endl;
        do
        {
            std::cout << *(node->item) << std::endl;
            node = node->next;

        } while ( node != NULL );
        std::cout << "-------- KONEC VYPISU ------------" << std::endl;      
    }
    else
    {
        std::cout << "Fronta je prazdna!" << std::endl;
    }
}

CCustomer * CQueue::GetFirst() const
{
    if ( !IsEmpty() )
    {
        return const_cast<CCustomer*>(m_first->item);
    }
    return 0;
}

void CQueue::ResetPointer()
{
    m_pointer = m_first;
}

void CQueue::MovePointerForward()
{
    if ( m_pointer )
    {
        m_pointer = m_pointer->next;
    }
    else
    {
        m_pointer = NULL;
    }
}

void CQueue::MovePointerBack()
{
    if ( m_pointer )
    {
        m_pointer = m_pointer->previous;
    }
    else
    {
        m_pointer = NULL;
    }
}

CCustomer * CQueue::GetCustomer() const
{
    CCustomer * customer;
    if ( m_pointer != NULL )
    {
        customer = const_cast<CCustomer *> (m_pointer->item);
        return customer;
    }
    else
    {
        return NULL;
    }
}