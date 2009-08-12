#ifndef _THREAD_H_
#define _THREAD_H_

#include <iostream>
#include "windows.h"

#define MAX_THREADS 2

DWORD WINAPI thread_function(LPVOID param);

// semaphore
struct sem_t
{
    bool open;

    sem_t(bool _open = true) : open(_open) {}
};

// shared data - multithreading safe (almost)
struct data_t
{
    struct sem_t sem;
    
    data_t(const char* _str)
    {
        size_t len = sizeof(str);
        strncpy(str, _str, len);
        str[len] = '\0';
    }

    bool lock()
    {
        while (!sem.open)
        {
            printf("\nSem closed - waiting 0.5s ...");
            Sleep(500);
        }
        sem.open = false;
        
        printf("\nSem open - access granted!\n");
        return true;
    }

    bool unlock()
    {
        sem.open = true;
        printf("\nSem closed - playground free!\n");
        return true;
    }

    const char* val()
    {
        return str;
    }

private:
    char str[200+1];
};

#endif