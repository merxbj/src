#include "thread.h"

DWORD WINAPI thread_function(LPVOID param)
{
    Sleep(1000); // do something
    data_t* shared_data = reinterpret_cast<data_t*>(param);

    shared_data->lock();
    printf("\n%s\n", shared_data->val());
    Sleep(5000); // do something with locked data
    shared_data->unlock();

    return 0;
}