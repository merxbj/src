#ifndef _FILE_CHECKER_SVCS_H_
#define _FILE_CHECKER_SVCS_H_

#include "TCHAR.H"

SERVICE_STATUS ServiceStatus; 
SERVICE_STATUS_HANDLE hStatus; 
 
void StartService();
void ServiceMain(int argc, char** argv); 
void ControlHandler(DWORD request);
bool InitService();
bool RegisterService(TCHAR* serviceName, TCHAR* serviceDisplayName, TCHAR* serviceBinPath);
void Run();

#endif