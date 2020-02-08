#ifndef _FILE_CHECKER_SVCS_H_
#define _FILE_CHECKER_SVCS_H_

#include "TCHAR.H"

#define WM_NOTIFY_ICON WM_USER+1;

enum
{
	ERROR_NONE,
	ERROR_INVALID_ARGS,
	ERROR_INVALID_REQUEST,
	ERROR_REGISTER_SERVICE_CTRL_HANDLER,
	ERROR_INIT_SERVICE,
	ERROR_OPEN_SC_MANAGER,
	ERROR_CREATE_SERVICE,
	ERROR_OPEN_SERVICE,
	ERROR_DELETE_SERVICE,
	ERROR_FATAL_ERROR,
	ERROR_GET_MODULE_FILE_NAME
};

int g_errorCode = ERROR_NONE;

const char LOG_FILE_PATH[] = "C:\\temp\\error.log";

SERVICE_STATUS ServiceStatus; 
SERVICE_STATUS_HANDLE hStatus; 
 
typedef enum RequestType_t
{
	REQUEST_INVALID = -1,
	NO_REQUEST = 0,
	REQUEST_REGISTER,
	REQUEST_UNREGISTER,
	REQUEST_RUN
} RequestType;

void RunService();
void ServiceMain(int argc, char** argv); 
void ControlHandler(DWORD request);
bool InitService();
int RegisterService(const TCHAR* serviceName, const TCHAR* serviceDisplayName);
int UnregisterService(TCHAR* serviceName);
void Run();
RequestType ParseCallerParameters(int argc, LPTSTR* argv);

int HandleErrorCode(int errorCode);

void log(TCHAR* message);

#endif