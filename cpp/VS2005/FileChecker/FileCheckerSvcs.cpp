#include <windows.h>
#include <stdio.h>
#include "FileCheckerSvcs.h"

void main(int argc, char** argv) 
{ 
    switch (ParseCallerParameters(argc, argv))
	{
	case REGISTER_SERVICE:
		RegisterService(L"FileChecker", L"File Checker", L"");
		break;
}

void StartService()
{
	SERVICE_TABLE_ENTRY ServiceTable[2];
    ServiceTable[0].lpServiceName = L"FileChecker";
    ServiceTable[0].lpServiceProc = (LPSERVICE_MAIN_FUNCTION)ServiceMain;

    ServiceTable[1].lpServiceName = NULL;
    ServiceTable[1].lpServiceProc = NULL;
    
	// Start the control dispatcher thread for our service
    StartServiceCtrlDispatcher(ServiceTable);
}


void ServiceMain(int argc, char** argv)
{
    int error; 
 
    ServiceStatus.dwServiceType        = SERVICE_WIN32; 
    ServiceStatus.dwCurrentState       = SERVICE_START_PENDING; 
    ServiceStatus.dwControlsAccepted   = SERVICE_ACCEPT_STOP | SERVICE_ACCEPT_SHUTDOWN;
    ServiceStatus.dwWin32ExitCode      = NO_ERROR; 
    ServiceStatus.dwServiceSpecificExitCode = NO_ERROR; 
    ServiceStatus.dwCheckPoint         = 0; 
    ServiceStatus.dwWaitHint           = 0; 
 
    hStatus = RegisterServiceCtrlHandler(
		L"FileChecker", 
		(LPHANDLER_FUNCTION)ControlHandler); 
    if (hStatus == (SERVICE_STATUS_HANDLE)0) 
    { 
        // Registering Control Handler failed
        return; 
    }  
    // Initialize Service 
    error = InitService(); 
    if (error) 
    {
		// Initialization failed
        ServiceStatus.dwCurrentState       = SERVICE_STOPPED; 
        ServiceStatus.dwWin32ExitCode      = -1; 
        SetServiceStatus(hStatus, &ServiceStatus); 
        return; 
    } 
    // We report the running status to SCM. 
    ServiceStatus.dwCurrentState = SERVICE_RUNNING; 
    SetServiceStatus (hStatus, &ServiceStatus);
 
	Run();

    return; 
}
 
// Service initialization
bool InitService() 
{
    return true;
}

// Control handler function
void ControlHandler(DWORD request) 
{
    switch(request)
    { 
        case SERVICE_CONTROL_STOP: 

            ServiceStatus.dwWin32ExitCode = 0; 
            ServiceStatus.dwCurrentState  = SERVICE_STOPPED; 
            SetServiceStatus (hStatus, &ServiceStatus);
            return; 
 
        case SERVICE_CONTROL_SHUTDOWN: 

            ServiceStatus.dwWin32ExitCode = 0; 
            ServiceStatus.dwCurrentState  = SERVICE_STOPPED; 
            SetServiceStatus (hStatus, &ServiceStatus);
            return; 
        
        default:
            break;
    } 
 
    // Report current status
    SetServiceStatus (hStatus,  &ServiceStatus);
 
    return; 
} 

bool RegisterService(const TCHAR* serviceName, const TCHAR* serviceDisplayName, const TCHAR* serviceBinPath)
{
	if ((_tcslen(serviceName) == 0) ||
		(_tcslen(serviceDisplayName) == 0) ||
		(_tcslen(serviceBinPath) == 0))
	{
		return false;
	}

	SC_HANDLE scmH = OpenSCManager(NULL, NULL, SC_MANAGER_CONNECT);

	if (scmH == NULL)
		return false;

	SC_HANDLE scH = CreateService(scmH,
		serviceName,
		serviceDisplayName,
		SC_MANAGER_CREATE_SERVICE,
		SERVICE_WIN32,
		SERVICE_AUTO_START,
		SERVICE_ERROR_IGNORE,
		serviceBinPath,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL);
	
	if (scH == NULL)
		return false;

	return true;
}

void Run()
{
}