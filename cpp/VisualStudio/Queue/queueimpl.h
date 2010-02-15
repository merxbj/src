#ifndef _queueimpl_h_
#define _queueimpl_h_
#pragma once

#include "queue.h"

struct Node 
{
    const CCustomer * item;
    struct Node * next;
    struct Node * previous;
};

class CQueue 
{

private:

    const unsigned int m_MAX_ITEMS;
    unsigned int m_numberOfItems;       
    Node * m_first;
    Node * m_last;
    Node * m_pointer;

public:

    CQueue(Node * newFirst = NULL, 
        Node * newLast = NULL,
        const unsigned int maxItems = 20);			   
    bool AddToQueue(const CCustomer &);
    CCustomer * RemoveFromQueue();
    unsigned int NumberOfItems() const;
    bool IsEmpty() const;
    bool IsFull() const;
    void PrintList() const;
    CCustomer * GetFirst() const;
    void ResetPointer();
    void MovePointerForward();
    void MovePointerBack();
    CCustomer * GetCustomer() const;
};

#endif
