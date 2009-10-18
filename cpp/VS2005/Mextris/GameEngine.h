#ifndef _GAMEENGINE_H_
#define	_GAMEENGINE_H_

class GameEngine
{
public:

	LRESULT ProcessMessages(UINT message, WPARAM wParam, LPARAM lParam);
	static GameEngine* GetInstance();
	void SetMainWindow(HWND hWnd);
	void Run();

private:

	static GameEngine* gameEngine;
	GameEngine();
	HWND mainWindow;
};

#endif