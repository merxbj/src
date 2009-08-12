#include "thread.h"

int main()
{
    HANDLE threads[MAX_THREADS];
    DWORD thread_ids[MAX_THREADS];
    data_t data("Working hard with acquired data!");
    
    for (int i = 0; i < MAX_THREADS; i++)
    {
        threads[i] = CreateThread(NULL, 0, thread_function, &data, 0, &thread_ids[i]);
    }

    
    
    printf("Main!");
    
    

    WaitForMultipleObjects(MAX_THREADS, threads, TRUE, INFINITE);
    
    for (int j = 0; j < MAX_THREADS; j++)
    {
        CloseHandle(threads[j]);
    }

    system("PAUSE");
    return 0;
}
