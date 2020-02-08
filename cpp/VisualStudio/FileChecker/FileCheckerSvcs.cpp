#include <windows.h>
#include <stdio.h>
#include "resource.h"
#include "FileCheckerSvcs.h"

int _tmain(int argc, LPTSTR* argv)
{
	g_errorCode = ERROR_NONE;

	switch (ParseCallerParameters(argc, argv))
	{
	case REQUEST_REGISTER:
		g_errorCode = RegisterService(L"FileChecker", L"File Checker");
		break;

	case REQUEST_UNREGISTER:
		g_errorCode = UnregisterService(L"FileChecker");
		break;

	case REQUEST_RUN:
		RunService();
		break;

	default:
		g_errorCode = ERROR_INVALID_REQUEST;
	}

	return HandleErrorCode(g_errorCode);
}

void RunService()
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
        g_errorCode = ERROR_REGISTER_SERVICE_CTRL_HANDLER;
		return;
    }  
    // Initialize Service 
    if (!InitService()) 
    {
		// Initialization failed
        ServiceStatus.dwCurrentState       = SERVICE_STOPPED; 
        ServiceStatus.dwWin32ExitCode      = -1; 
        SetServiceStatus(hStatus, &ServiceStatus); 
		g_errorCode = ERROR_INIT_SERVICE;
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

int RegisterService(const TCHAR* serviceName, const TCHAR* serviceDisplayName)
{
	if ((_tcslen(serviceName) == 0) ||
		(_tcslen(serviceDisplayName) == 0))
	{
		return ERROR_INVALID_ARGS;
	}

	TCHAR szPath[MAX_PATH+1] = TEXT("");
	if (!GetModuleFileName( NULL, szPath, MAX_PATH))
	{
        return ERROR_GET_MODULE_FILE_NAME;
	}


	SC_HANDLE scmH = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (scmH == NULL)
		return ERROR_OPEN_SC_MANAGER;

	SC_HANDLE scH = CreateService(scmH,
		serviceName,
		serviceDisplayName,
		SERVICE_ALL_ACCESS,
		SERVICE_WIN32_OWN_PROCESS,
		SERVICE_DEMAND_START,
		SERVICE_ERROR_NORMAL,
		szPath,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL);
	
	if (scH == NULL)
		return ERROR_CREATE_SERVICE;

	CloseServiceHandle(scmH);
	CloseServiceHandle(scH);

	return 0;
}

RequestType ParseCallerParameters(int argc, LPTSTR* argv)
{
	if (argc == 1)
		return REQUEST_RUN;

	if (argc > 2)
		return REQUEST_INVALID;

	if (argc == 2)
	{
		if (_tcscmp(argv[1], L"/i") == 0)
			return REQUEST_REGISTER;
		else if (_tcscmp(argv[1], L"/u") == 0)
			return REQUEST_UNREGISTER;
		else
			return REQUEST_INVALID;
	}

	return REQUEST_INVALID;
}

int UnregisterService(TCHAR* serviceName)
{
	if (_tcslen(serviceName) == 0) 
	{
		return ERROR_INVALID_ARGS;
	}

	SC_HANDLE scmH = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);

	if (scmH == NULL)
		return ERROR_OPEN_SC_MANAGER;

	SC_HANDLE scH = OpenService(scmH, serviceName, SERVICE_ALL_ACCESS);
	
	if (scH == NULL)
		return ERROR_OPEN_SERVICE;

	return (DeleteService(scH) == TRUE) ? ERROR_NONE : ERROR_DELETE_SERVICE;
}

int HandleErrorCode(int errorCode)
{
	switch (errorCode)
	{
	case ERROR_NONE:
		break; // we will not log succeesses
	case ERROR_INVALID_ARGS:
		log(L"Programmer mistake - function got invalid arguments.\n");
		break;
	case ERROR_INVALID_REQUEST:
		log(L"User mistake - program got invalid arguments.\n");
		break;
	case ERROR_REGISTER_SERVICE_CTRL_HANDLER:
		log(L"System error - program couldn't start the service.\n");
		break;
	case ERROR_INIT_SERVICE:
		log(L"Application error - couldn't initialize the service.\n");
		break;
	case ERROR_OPEN_SC_MANAGER:
		log(L"System error - program couldn't open the service controll manager.\n");
		break;
	case ERROR_CREATE_SERVICE:
		log(L"System error - program couldn't register a service.\n");
		break;
	case ERROR_OPEN_SERVICE:
		log(L"System error - program couldn't open a service.\n");
		break;
	case ERROR_DELETE_SERVICE:
		log(L"System error - program couldn't delete a service.\n");
		break;
	case ERROR_GET_MODULE_FILE_NAME:
		log(L"System error - program couldn't retrieve a module file path.\n");
		break;
	default:
		log(L"Fatal error - program encountered a serious unhandler errror.\n");
	}
	
	return errorCode;
}

void log(TCHAR* message)
{
	if (message == NULL)
		return;
	
	FILE* f = fopen(LOG_FILE_PATH, "a+");
	if (f == NULL)
		return;

	_ftprintf(f, L"%s\n", message);

	fclose(f);
}

void Run()
{
	while (ServiceStatus.dwCurrentState == SERVICE_RUNNING)
	{
		log(L"Up and running!");
		/* Add file checking code here */
		Sleep(5000);
	}
}